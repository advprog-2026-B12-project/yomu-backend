package id.ac.ui.cs.advprog.yomubackend.discussion.service;

import java.util.UUID;

public interface UserLookup {

    UUID resolveUserId(String username);
}
