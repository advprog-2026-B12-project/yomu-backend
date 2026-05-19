package id.ac.ui.cs.advprog.yomubackend.comment.service;

import java.util.UUID;

public interface UserLookup {

    UUID resolveUserId(String username);
}
