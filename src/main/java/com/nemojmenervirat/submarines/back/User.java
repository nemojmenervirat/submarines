package com.nemojmenervirat.submarines.back;

import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class User implements Serializable {

  private final UUID id = UUID.randomUUID();
  private String username;

}
