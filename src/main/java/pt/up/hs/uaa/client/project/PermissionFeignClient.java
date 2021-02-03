package pt.up.hs.uaa.client.project;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.up.hs.uaa.client.AuthorizedUserFeignClient;
import pt.up.hs.uaa.client.project.dto.ProjectPermissionsDTO;

import java.util.List;

@AuthorizedUserFeignClient(name = "project")
public interface PermissionFeignClient {

    @RequestMapping(value = "/api/permissions/{user}", method = RequestMethod.GET)
    List<ProjectPermissionsDTO> getUserPermissions(@PathVariable("user") String user);

    @RequestMapping(value = "/api/permissions/{user}/connections", method = RequestMethod.GET)
    List<String> getUserConnections(@PathVariable("user") String user);

    @RequestMapping(value = "/api/projects/{projectId}/permissions", method = RequestMethod.GET)
    List<ProjectPermissionsDTO> getPermissionsForProject(@PathVariable("projectId") Long projectId);
}
