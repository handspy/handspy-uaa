package pt.up.hs.uaa.client.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import pt.up.hs.uaa.client.project.dto.ProjectPermissionsDTO;

import java.util.List;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ProjectMicroService {

    private final Logger log = LoggerFactory.getLogger(ProjectMicroService.class);

    private final PermissionFeignClient permissionFeignClient;

    @Autowired
    public ProjectMicroService(
        PermissionFeignClient permissionFeignClient
    ) {
        this.permissionFeignClient = permissionFeignClient;
    }

    @Cacheable(value = "project.permissions", key = "#projectId", sync = true)
    public List<ProjectPermissionsDTO> projectPermissions(Long projectId) {
        log.info("Finding permissions for project {}", projectId);

        return permissionFeignClient
            .getPermissionsForProject(projectId);
    }

    @Cacheable(value = "user.permissions", key = "#user", sync = true)
    public List<ProjectPermissionsDTO> userPermissions(String user) {
        log.info("Finding permissions for user {}", user);

        return permissionFeignClient
            .getUserPermissions(user);
    }

    @Cacheable(value = "user.connections", key = "#user", sync = true)
    public List<String> userConnections(String user) {
        log.info("Finding connections for user {}", user);

        return permissionFeignClient.getUserConnections(user);
    }
}
