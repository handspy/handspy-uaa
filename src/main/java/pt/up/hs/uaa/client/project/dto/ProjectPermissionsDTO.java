package pt.up.hs.uaa.client.project.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A DTO that contains the permissions of a user towards a project.
 *
 * @author José Carlos Paiva
 */
public class ProjectPermissionsDTO implements Serializable {

    /**
     * User to which this permission is assigned.
     */
    private String user;

    /**
     * A permission (project) refers to a project.
     */
    private Long projectId;

    /**
     * The permission of this entry.
     */
    private List<String> permissions = new ArrayList<>();

    public ProjectPermissionsDTO() {
    }

    public ProjectPermissionsDTO(String user, Long projectId, List<String> permissions) {
        this.user = user;
        this.projectId = projectId;
        this.permissions = permissions;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectPermissionsDTO that = (ProjectPermissionsDTO) o;
        return Objects.equals(user, that.user) &&
            Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, projectId);
    }

    @Override
    public String toString() {
        return "ProjectPermissionsDTO{" +
            ", user=" + getUser() +
            ", projectId=" + getProjectId() +
            ", permissions=" + getPermissions() +
            "}";
    }
}
