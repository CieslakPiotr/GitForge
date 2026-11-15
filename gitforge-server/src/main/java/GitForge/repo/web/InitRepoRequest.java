package GitForge.repo.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

record InitRepoRequest(
        @NotBlank
        @Pattern(regexp = "(?!\\.{1,2}$)[A-Za-z0-9._-]{1,100}") String name) {}
