package com.facebook.buck_project_builder.targets;

import com.facebook.buck_project_builder.FileSystem;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class RemoteFileTarget implements BuildTarget {

  private static final Logger LOGGER = Logger.getGlobal();

  private final String url;

  RemoteFileTarget(String url) {
    this.url = url;
  }

  static RemoteFileTarget parse(JsonObject targetJsonObject) {
    return new RemoteFileTarget(targetJsonObject.get("url").getAsString());
  }

  @Override
  public void build(String buckRoot, String outputDirectory) {
    File outputDirectoryFile = new File(outputDirectory);
    try {
      FileSystem.unzipRemoteFile(url, outputDirectoryFile);
    } catch (IOException firstException) {
      try {
        FileSystem.unzipRemoteFile(url, outputDirectoryFile);
      } catch (IOException secondException) {
        LOGGER.warning(
            String.format(
                "Cannot fetch and unzip remote python dependency at `%s` after 1 retry.", url));
        LOGGER.warning("First IO Exception: " + firstException);
        LOGGER.warning("Second IO Exception: " + secondException);
      }
    }
  }

  @Override
  public String toString() {
    return String.format("{url=%s}", url);
  }

  @Override
  public boolean equals(@Nullable Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    RemoteFileTarget remoteFileTarget = (RemoteFileTarget) other;
    return url.equals(remoteFileTarget.url);
  }

  @Override
  public int hashCode() {
    return url.hashCode();
  }
}