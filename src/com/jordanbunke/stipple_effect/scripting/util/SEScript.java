package com.jordanbunke.stipple_effect.scripting.util;

import com.jordanbunke.delta_time.scripting.ast.nodes.function.HeadFuncNode;

import java.nio.file.Path;

public record SEScript(HeadFuncNode head, Path path) {
}
