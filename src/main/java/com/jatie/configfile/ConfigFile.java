package com.jatie.configfile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.stream.Stream;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConfigFile {
    private String apiKey;
    private String osuDirectory;
    private String username;
    private String password;
}
