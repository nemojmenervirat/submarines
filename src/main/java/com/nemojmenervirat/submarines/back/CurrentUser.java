package com.nemojmenervirat.submarines.back;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Getter;
import lombok.Setter;

@UIScope
@Component
@Getter
@Setter
public class CurrentUser extends User {

  @PostConstruct
  void init() {
    setUsername("anonymous_" + System.nanoTime());
  }


}
