package pt.up.hs.uaa.service;

import io.github.jhipster.security.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Status;
import pt.up.hs.uaa.client.project.ProjectMicroService;
import pt.up.hs.uaa.client.project.dto.ProjectPermissionsDTO;
import pt.up.hs.uaa.config.Constants;
import pt.up.hs.uaa.constants.EntityNames;
import pt.up.hs.uaa.constants.ErrorKeys;
import pt.up.hs.uaa.domain.Authority;
import pt.up.hs.uaa.domain.LengthUnit;
import pt.up.hs.uaa.domain.TimeUnit;
import pt.up.hs.uaa.domain.User;
import pt.up.hs.uaa.repository.AuthorityRepository;
import pt.up.hs.uaa.repository.UserRepository;
import pt.up.hs.uaa.security.AuthoritiesConstants;
import pt.up.hs.uaa.security.SecurityUtils;
import pt.up.hs.uaa.service.dto.UserDTO;
import pt.up.hs.uaa.service.exceptions.ServiceException;
import pt.up.hs.uaa.service.util.SearchCriteria;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pt.up.hs.uaa.config.Constants.INTERNAL_CLIENT_ID;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    private final PasswordEncoder passwordEncoder;

    private final CacheManager cacheManager;

    private final ProjectMicroService projectMicroService;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager,
        ProjectMicroService projectMicroService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
        this.projectMicroService = projectMicroService;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                this.clearUserCaches(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                this.clearUserCaches(user);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        Optional<User> userOptional = userRepository.findOneByLogin(mail.toLowerCase());
        if (!userOptional.isPresent()) {
            userOptional = userRepository.findOneByEmailIgnoreCase(mail);
        }
        return userOptional
            .filter(User::getActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                this.clearUserCaches(user);
                return user;
            });
    }

    public User registerUser(UserDTO userDTO, String password) {
        if (userDTO.getLogin().equalsIgnoreCase(INTERNAL_CLIENT_ID)) {
            throw new UsernameAlreadyUsedException();
        }
        userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new UsernameAlreadyUsedException();
            }
        });
        userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new EmailAlreadyUsedException();
            }
        });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setOrganization(userDTO.getOrganization());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setCountry(userDTO.getCountry());
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        if (userDTO.getLengthUnit() == null) {
            newUser.setLengthUnit(Constants.DEFAULT_LENGTH_UNIT);
        } else {
            newUser.setLengthUnit(userDTO.getLengthUnit());
        }
        if (userDTO.getTimeUnit() == null) {
            newUser.setTimeUnit(Constants.DEFAULT_TIME_UNIT);
        } else {
            newUser.setTimeUnit(userDTO.getTimeUnit());
        }
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        this.clearUserCaches(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.getActivated()) {
             return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        this.clearUserCaches(existingUser);
        return true;
    }

    public User createUser(UserDTO userDTO) {
        if (userDTO.getLogin().equalsIgnoreCase(INTERNAL_CLIENT_ID)) {
            throw new UsernameAlreadyUsedException();
        }
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setOrganization(userDTO.getOrganization());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setCountry(userDTO.getCountry());
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        if (userDTO.getLengthUnit() == null) {
            user.setLengthUnit(Constants.DEFAULT_LENGTH_UNIT);
        } else {
            user.setLengthUnit(userDTO.getLengthUnit());
        }
        if (userDTO.getTimeUnit() == null) {
            user.setTimeUnit(Constants.DEFAULT_TIME_UNIT);
        } else {
            user.setTimeUnit(userDTO.getTimeUnit());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO.getAuthorities().stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName         first name of user.
     * @param lastName          last name of user.
     * @param organization      organization of user.
     * @param email             email id of user.
     * @param country           country of user.
     * @param langKey           language key.
     * @param lengthUnit        preferred length unit.
     * @param timeUnit          preferred time unit.
     * @param imageUrl          image URL of user.
     */
    public void updateUser(
        String firstName, String lastName,
        String organization,
        String email,
        String country,
        String langKey,
        LengthUnit lengthUnit,
        TimeUnit timeUnit,
        String imageUrl
    ) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setOrganization(organization);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setCountry(country);
                user.setLangKey(langKey);
                user.setLengthUnit(lengthUnit);
                user.setTimeUnit(timeUnit);
                user.setImageUrl(imageUrl);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
            });
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository
            .findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                this.clearUserCaches(user);
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setOrganization(userDTO.getOrganization());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setCountry(userDTO.getCountry());
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                user.setLengthUnit(userDTO.getLengthUnit());
                user.setTimeUnit(userDTO.getTimeUnit());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO.getAuthorities().stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(user -> {
            userRepository.delete(user);
            this.clearUserCaches(user);
            log.debug("Deleted User: {}", user);
        });
    }

    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                this.clearUserCaches(user);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findAllUsers() {
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            return getAllManagedUsers();
        }
        Optional<String> optCurrentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!optCurrentUserLogin.isPresent()) {
            return new ArrayList<>();
        }
        String currentUserLogin = optCurrentUserLogin.get();
        Map<String, UserDTO> users = getAllManagedUsers().parallelStream()
            .collect(Collectors.toMap(UserDTO::getLogin, Function.identity()));
        List<String> userConnections = projectMicroService
            .userConnections(currentUserLogin);
        return users.entrySet().parallelStream()
            .filter(user -> !user.getKey().equals(Constants.SYSTEM_ACCOUNT))
            .map(e -> {
                if (userConnections.contains(e.getKey()) || e.getKey().equals(currentUserLogin)) {
                    return e.getValue();
                }
                UserDTO userDTO = new UserDTO();
                userDTO.setId(e.getValue().getId());
                userDTO.setLogin(e.getValue().getLogin());
                return userDTO;
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllManagedUsers() {
        return userRepository
            .findAllByLoginNot(Constants.ANONYMOUS_USER).stream()
            .map(UserDTO::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> searchUsers(List<SearchCriteria> params) {
        Optional<String> optCurrentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!optCurrentUserLogin.isPresent()) {
            return new ArrayList<>();
        }
        String currentUserLogin = optCurrentUserLogin.get();
        Map<String, User> users = userRepository.search(params).parallelStream()
            .collect(Collectors.toMap(User::getLogin, Function.identity()));
        List<String> userConnections = projectMicroService
            .userConnections(currentUserLogin);
        return users.entrySet().parallelStream()
            .map(e -> {
                if (userConnections.contains(e.getKey())) {
                    return new UserDTO(e.getValue());
                }
                UserDTO userDTO = new UserDTO();
                userDTO.setId(e.getValue().getId());
                userDTO.setLogin(e.getValue().getLogin());
                return userDTO;
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getConnections() {
        Optional<String> optCurrentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!optCurrentUserLogin.isPresent()) {
            return new ArrayList<>();
        }
        String currentUserLogin = optCurrentUserLogin.get();
        List<String> userConnections = projectMicroService
            .userConnections(currentUserLogin);
        return userRepository.findAllByLoginIsIn(userConnections).stream()
            .map(UserDTO::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersInProject(Long projectId) {
        Optional<String> optCurrentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (!optCurrentUserLogin.isPresent()) {
            return new ArrayList<>();
        }
        String currentUserLogin = optCurrentUserLogin.get();
        List<ProjectPermissionsDTO> projectPermissions = projectMicroService
            .projectPermissions(projectId);
        boolean allowed = projectPermissions.parallelStream()
            .anyMatch(projectPermission ->
                projectPermission.getUser().equals(currentUserLogin) &&
                    !projectPermission.getPermissions().isEmpty()
            );
        if (!allowed) {
            throw new ServiceException(Status.FORBIDDEN, EntityNames.USER, ErrorKeys.ERR_PERMISSION_NOT_AVAILABLE, "Not allowed to perform this operation.");
        }
        List<String> users = projectPermissions.parallelStream()
            .map(ProjectPermissionsDTO::getUser)
            .collect(Collectors.toList());
        return userRepository.findAllByLoginIsIn(users).stream()
            .map(UserDTO::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(Long id) {
        return userRepository.findOneWithAuthoritiesById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                log.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
                this.clearUserCaches(user);
            });
    }

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }
}
