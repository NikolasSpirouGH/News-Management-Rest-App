package com.news.payload;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenRequestRefresh {
    String oldToken;
}
