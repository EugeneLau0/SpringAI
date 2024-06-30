package org.away.service;

import java.util.Map;

public interface AbstractTongyiService {

    String completion(String content);

    Map<String, String> streamCompletion(String content);
}
