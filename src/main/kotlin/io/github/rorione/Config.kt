package io.github.rorione

import com.typesafe.config.Config

data class Config(
    val nodeUrl: String,
    val oracles: Map<String, String>,
) {
    constructor(config: Config) : this(
        nodeUrl = config.getString("app.node_url"),
        oracles = config.getConfig("app.oracles")
            .entrySet()
            .associate { (k, v) ->
                k to v.unwrapped().toString()
            },
    )
}
