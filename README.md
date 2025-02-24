# ![Stipple Effect](https://raw.githubusercontent.com/stipple-effect/docs/master/assets/graphics/logo-anim.gif)

[![Website](https://raw.githubusercontent.com/stipple-effect/docs/master/assets/buttons/website.png)](https://stipple-effect.github.io)
[![Changelog](https://raw.githubusercontent.com/stipple-effect/docs/master/assets/buttons/changelog.png)](changelog.md)
[![Roadmap](https://raw.githubusercontent.com/stipple-effect/docs/master/assets/buttons/roadmap.png)](roadmap.md)
[![Buy on Itch.io](https://raw.githubusercontent.com/stipple-effect/docs/master/assets/buttons/itch.png)](https://flinkerflitzer.itch.io/stipple-effect)

## About
*Stipple Effect* is a pixel art editor that supports animation and scripting. It is designed to facilitate a variety of workflows and to encourage rapid, iterative creation of video game art assets and other types of artwork.

* Made by a solo game developer with the needs and skill sets of other indie devs in mind
* Lightweight and simple to learn and use, yet allows for considerable depth and complexity
* Expressive scripting API that can be used to **automate program actions** and **create dynamic preview and color transformations with complex logic**

![Preview](https://raw.githubusercontent.com/stipple-effect/docs/master/assets/graphics/complex-preview.gif)
<div align="center">A preview script in action</div>

## Features
* Symbiotic relationship between [layers](https://stipple-effect.github.io/docs/layer) and [frames](https://stipple-effect.github.io/docs/frame)
    * Linked-cel layers (contents persist across frames)
* Two-color system: *Stipple Effect*'s brush, pencil, and gradient tool allow for interesting [combinations](https://stipple-effect.github.io/docs/color#combination-modes) of the [primary and secondary colors](https://stipple-effect.github.io/docs/interface#system-colors)
* Project [state control](https://stipple-effect.github.io/docs/state-control)
    * [Granular undo and redo](https://stipple-effect.github.io/docs/state-control#granularity)
    * History
    * Generate time lapses
* Animation
    * [Onion skinning](https://stipple-effect.github.io/docs/layer#onion-skin)
    * Edit during playback
* Split/stitch
    * [Split a sprite sheet into frames](https://stipple-effect.github.io/docs/sizing#split-a-sprite-sheet-into-frames)
    * [Stitch an animation into a sprite sheet](https://stipple-effect.github.io/docs/sizing#stitch-an-animation-into-a-sprite-sheet)
* Palettes
    * Import and export palettes
    * [Palettization](https://stipple-effect.github.io/docs/color-actions#palettization)
    * [Extract colors in a project to a palette](https://stipple-effect.github.io/docs/color-actions#extract-canvas-colors-to-palette)
* [Selection](https://stipple-effect.github.io/docs/selection)
    * [Family of selection tools](https://stipple-effect.github.io/docs/sel-area-tools) designed for pixel-perfect selection
    * Intuitive and powerful [outlining utilities](https://stipple-effect.github.io/docs/outline)
* [Pixel grid](https://stipple-effect.github.io/docs/pixel-grid)
    * Easily enable/disable and modify the size of the pixel grid's cells
    * The box select tool can snap to the grid
* And much more!

Find out more about the program by reading [the documentation](https://stipple-effect.github.io/docs/).

## Scripting
*Stipple Effect* scripts have three main uses: [**automation**](https://stipple-effect.github.io/docs/automation-scripts), [**custom previews**](https://stipple-effect.github.io/docs/preview-scripts) and [**color transformations**](https://stipple-effect.github.io/docs/color-scripts). It is highly recommended for users that want to get the most out of the program to read the API specification and familiarize themselves with its potential applications.
* [Scripting overview](https://stipple-effect.github.io/docs/scripting)
* [API specification](https://stipple-effect.github.io/api/)
* [Script examples](https://github.com/stipple-effect/script-examples)

I have created [a VS Code extension](https://marketplace.visualstudio.com/items?itemName=jordanbunke.deltascript-for-stipple-effect) that provides syntax highlighting for *Stipple Effect* scripts.

## External Dependencies
* [Delta Time](https://github.com/jbunke/delta-time) - General-purpose library that handles GUI and execution loop boilerplate
* [STIP Parser](https://github.com/stipple-effect/stip-parser) - Writes *Stipple Effect* projects and palettes to file and parses `.stip` and `.stippal` files
* [ANTLR v4](https://github.com/antlr/antlr4) - Lexing and parsing library that powers the _DeltaScript_ interpreter
* [Animation Encoder](https://github.com/jbunke/animation-encoder) - My wrapper for Square's [gifencoder](https://github.com/square/gifencoder) and for [jcodec](https://github.com/jcodec/jcodec)

## License

*Stipple Effect* is distributed under an [end-user license agreement](./LICENSE) (EULA). Read it to understand your rights and responsibilities as a user.

## Compile from source instructions

For those who do not have the means to buy *Stipple Effect*, the program can be compiled from source for free:

### Instructions

1. Clone this repository
2. Download and configure external dependencies in your development environment
   1. **Delta Time**: [Download JAR - instructions in README](https://github.com/jbunke/delta-time)
   2. **STIP Parser**: [Download JAR - instructions in README](https://github.com/stipple-effect/stip-parser)
   3. **Animation Encoder**: [Download JAR - instructions in README](https://github.com/jbunke/animation-encoder)
   4. **ANTLR v4**: *Stipple Effect* uses version 4.13.1 of the ANTLR 4 Runtime. You can download the library from the Maven Repository or configure it manually. Find the artifact's dependency information [here](https://mvnrepository.com/artifact/org.antlr/antlr4-runtime/4.13.1).
3. Set up the project and run configuration with the following details:
   1. **JRE / JDK / Language level**: Java 17 or later
   2. **Main class**: `com.jordanbunke.stipple_effect.StippleEffect`
4. Build and run in the Java development environment of your choice!

### Note:
*Please keep in mind that you are liable to the terms of the EULA whether you buy the program or compile it from source.*
