package org.example.businessLogic;
import org.example.model.Server;
import org.example.model.Task;

import java.util.*;
public interface Strategy {
    public void addTask(List<Server> servers, Task t);
}
