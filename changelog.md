# Changelog

## **1.2.3** - 2025-01-30

### Added:
* *Reload script* button when running a script in the preview window: reload from file with changes
* Pointer finger cursor to select dialog buttons and navbar submenu terminal buttons

### Changed:
* Updated *Stipple Effect -> Resources* links with new repository and website URLs
* Updated *DeltaScript* interpreter

### Fixed:
* Bug: Additive operators have a higher precedence than multiplicative operators in {DeltaScript}
* Bug: Rendering the pixel grid can cause the program to crash

### API Changes:
* Changed:
  * Refactored `hsv` color generation functions from `$SE` to `$Graphics`
    * `$SE.hsv(float h, float s, float v, int a) -> color` to `$Graphics.hsv(float h, float s, float v, int a) -> color`
    * `$SE.hsv(float h, float s, float v) -> color` to `$Graphics.hsv(float h, float s, float v) -> color`

## **1.2.2** - 2024-12-31

### Added:
* Comprehensive [program documentation](https://stipple-effect.github.io/docs/) and [scripting API specification](https://stipple-effect.github.io/api/)

### Changed:
* Updated *Stipple Effect -> Resources* links with the new website

### Fixed:
* Bug: Line tool produces doubles at oblique angles when line breadth is 1

### API Changes:
* Added:
  * `light` type
  * New namespaces and associated functions and constants
    * `$Graphics`
    * `$Math`

## **1.2.1** - 2024-09-06

### Added:
* Minimum values are enforced on startup for settings that could lead to crashes
* Extended the Color Picker tool with two alternate modes
  * Holding **Shift** searches layer by layer for the highest non-transparent color at the specified pixel; does nothing if no such pixel is found
  * Holding **Ctrl** samples the pixel from the flattened project
* Normal map color sampler with quantization option

### Fixed:
* Bug: Attempting to validate a script with no return type crashes the program
* Bug: Crashes due to impossible layout/sizing logic

## **1.2.0** - 2024-09-03

### Added:
* Layer visibility actions to navbar under "Layer" -> "Visibility status"
* Bug reporting, scripting API, VS Code extension links to navbar under "Stipple Effect" -> "Resources"
* Setting for whether preview windows are opened in a separate window 
* Reimport a preview into Stipple Effect with layers preserved 
* Generate time lapses that track project changes 
* New brush shapes 
  * Square 
  * Line + angle 
* Added setting to propagate changes made to breadth tool properties to all tools; affected tools:
  * Brush 
  * Shade Brush 
  * Script Brush 
  * Eraser 
  * Line Tool 
  * Shape Tool 
  * Gradient Tool 
* Frames with non-standard durations show an asterisk (*) next to their frame number
* Added `{` and `}` to the illegal character set for names (filename, layer name, etc.)

### Changed:
* Overhauled and extended onion skin system 
  * Onion skins can now be rendered below or above the cel contents 
  * Onion skin types:
    * Standard translucent copy of contents 
    * Outline 
    * Tinted 
  * Render skins for up to 6 cels behind or ahead of the current frame index 
* Improved file dialogs on Windows 
* Combined file dialog file filters for **.stip** and image files into a single filter
* `(image -> image)` preview scripts can be run on animated projects 
* Cosmetic and layout changes 
  * Simplified and shortened color sampler labels 
  * Updated *Zo* theme colors 
  * Updated layer button layout and flipbook panel sizing 
* Preview window changes 
  * Preview windows are now resizeable 
  * Preview windows can run in the main program window or a separate window as before 
  * Preview windows now persist across project context switches 
* Palette colors are assigned differently according to the mouse button clicked:
  * Left click: primary color 
  * Right click: secondary color 
  * Middle click: selected color (most recently assigned or modified of primary and secondary)

### Fixed:
* Bug: `$SE.single_outline()` calls a double outline instead of a single outline
* Bug: void helper functions cannot be called
* Bug: suspended execution due to file dialog display leads to a playback backlog
* Bug: attempts to create the settings folder every time the program writes settings

### API Changes:
* Added:
  * New types
    * `script`
    * `save_config`
  * Constants 
    * Scope constants that evaluate to `int`
      * `$SE.PROJECT` = `0`
      * `$SE.LAYER` = `1`
      * `$SE.FRAME` = `2`
      * `$SE.CEL` = `3`
    * Save type constants that evaluate to `int`
      * `$SE.NATIVE` = `0`
      * `$SE.PNG_SHEET` = `1`
      * `$SE.PNG_SEPARATE` = `2`
      * `$SE.GIF` = `3`
      * `$SE.MP4` = `4`
    * Dimension constants that evaluate to `bool`
      * `$SE.HORZ` = `true`
      * `$SE.VERT` = `false`
  * Added `layer` functions; for some `layer L`...
    * ```js
      L.get_name() -> string
      ```
    * ```js
      L.set_name(string);
      ```
  * Added `project` functions; for some `project P`...
    * ```js
      P.get_width() -> int
      ```
    * ```js
      P.get_height() -> int
      ```
    * ```js
      P.get_layer(string) -> layer
      ```
    * ```js
      P.get_save_config() -> save_config
      ```
    * ```js
      P.save_as(save_config);
      ```
  * Added global functions
    * ```js
      $SE.read_script(string) -> script
      ```
    * ```js
      $SE.new_save_config(string[], string, int) -> save_config
      ```
    * ```js
      $SE.transform(project, script, bool, bool) -> project
      ```
    * ```js
      $SE.transform(project, script) -> project
      ```
    * ```js
      $SE.new_project(int, int, bool) -> project
      ```

* Changed:
  * Renamed `layer` functions; for some `layer L`... 
    * Renamed `L.get_frame(int)` to `L.get_cel(int)`
    * Renamed `L.set_frame(int, image)` to `L.set_cel(int, image)`
    * Renamed `L.wipe_frame(int, image)` to `L.wipe_cel(int, image)`
    * Renamed `L.edit_frame(int, image)` to `L.edit_cel(int, image)`
    * Renamed `L.link_frames()` to `L.link_cels()`
    * Renamed `L.unlink_frames()` to `L.unlink_cels()`
    * Renamed `L.disable_layer()` to `L.disable()`
    * Renamed `L.enable_layer()` to `L.enable()`

## **1.1.0** - The UI/UX Update - 2024-08-09

### Added:
* Draggable buttons: buttons can be clicked and dragged to resort...
  * Frames
  * Layers
  * Palette colors
* Cel buttons
  * A range of cels across multiple frames and layers can be selected
  * Cel selection operations: copy, cut, paste, delete
* Multiple color sampling modes
  * RGB-A sliders
  * HSV-A sliders
  * Saturation-value matrix
  * Color wheel
* Navigation bar
* Number key icons to first {9} project buttons

### Changed:
* Layout overhaul
  * The layers and frames panels have been replaced with the "flipbook" panel
  * The colors panel now displays the color sampler and the palette simultaneously
* The preset palettes are no longer preloaded; can now be accessed in the navigation bar

### Fixed:
* Bug: Tool mouse actions can still be executed when the cursor is in a dropdown menu
* Bug: Cannot use the brush tool without a selected palette

### API Changes:
* Removed:
  * Removed the property `mutable` of the type `palette`

* Changed:
  * API references to `LAYER-FRAME` have been renamed to `CEL`; no changed behaviour

## **1.0.1** - 2024-07-11

### Added:
* Zoom levels 125%, 150%, and 175% for smoother zooming
* Pressing *Esc* while a dialog menu is open will clear the dialog; equivalent of "Cancel" button

### Changed:
* 2x the program frame rate to 60 FPS
* Tool tips display after hovering over a button for 0.25 seconds, twice as fast as before

### Fixed:
* Bug: Script calls to `.keys()` not being handled correctly
* Bug: Box select tool does not select off-canvas pixels inside the selection bounds
* Bug: Reimporting a scripted preview inherits layer name from its source project

## **1.0.0** - Official Release - 2024-06-13

### Changed:
* Optimized Move Selection tool preview overlay
* Optimized selection overlay rendering
* Updated program logo

### Fixed:
* Bug: Breadth tool overlays are not redrawn at a zoom level of 100%

## **0.5.1** - 2024-06-12

### Fixed:
* Bug: Attempting to pick up a selection with pixels off the grid is causing the program to crash


## **0.5.0** - The Performance Update - 2024-06-12

### Added:
* Added tools
  * Shape tool (hotkey *R*)
  * Script brush (hotkey *Q*)
* Polygon Select now has a "close polygon" shortcut (*Shift + Left Click*)
* Images can now be pasted from the system clipboard
* Added themes:
  * Asylum
  * Ramallah
* Added "snap to target pixel" behaviour (*Shift + Enter*)
* Stretch operation can now be snapped to the pixel grid by holding *Shift*
* Added relative frame durations so some frames can be displayed for more or less time than others
* Added history dialog (*Shift + Y*) where users can browse all cached project states and revert to any of them
* Added playback controls to preview dialogs
* Added keyboard controls to the preview window's playback controls

### Changed:
* Optimized selection overlays
* Optimized selection logic
* Optimized search algorithm (Wand and Fill tools)
* Now discards intermediate (granular) project states after five new checkpoint project states
* Updated themes:
  * Zo
  * Neon
  * Bunkering
* Move selection and pick up selection action previews are rendered in a different style
* Changed frame limit from 100 to 300 and layer limit from 100 to 50
* Increased maximum canvas bounds to 1920 x 1080 pixels
* Improved selection transform node manipulation

### Fixed:
* Bug: PIXEL_GRID_ON_BY_DEFAULT setting flag is not persistent

### API Changes:
* Added:
  * `project` frame duration functions:
      * ```js
        P.get_frame_duration(int i) -> float
        ```
      * ```js
        P.get_frame_durations() -> float[]
        ```
      * ```js
        P.set_frame_duration(int i, float frame_duration);
        ```

* Changed:
  * Separated selection from scope in color actions
      * Modified `scope` enumeration:
        ```js
        0: PROJECT
        1: LAYER
        2: FRAME
        3: LAYER_FRAME
        ```
      * Changed `project` color action function signatures:
          * ```js
            P.palettize(palette pal, int scope, bool include_disabled, bool ignore_selection);
            ```
          * ```js
            P.extract_to_pal(palette pal, int scope, bool include_disabled, bool ignore_selection);
            ```
          * ```js
            P.hsv_shift(int scope, bool include_disabled, bool ignore_selection, int h_shift, N s_shift, N v_shift);
            ```
          * ```js
            P.color_script(int scope, bool include_disabled, bool ignore_selection, string script_path);
            ```

## **0.4.2** - 2024-05-26

### Added:
* Color scripts that encounter errors now display their errors

### Changed:
* Color action previews (HSV level shift, color script execution) now show transparency checkerboard behind previewed image

### API Changes:
**Note: This update completely redesigned the API and altered the syntax of every API function. Scripts that utilize API functions that were written for versions 0.4.0 or 0.4.1 will need to be rewritten in order to execute on subsequent versions of Stipple Effect.**

* Added:
  * `palette` type
  * Global namespace `$SE`
  * `project` color action functions:
    * ```js
      P.palettize(palette pal, int scope, bool include_disabled);
      ```
    * ```js
      P.extract_to_pal(palette pal, int scope, bool include_disabled);
      ```
    * ```js
      P.hsv_shift(int scope, bool include_disabled, int h_shift, float s_shift, float v_shift);
      ```
    * ```js
      P.color_script(int scope, bool include_disabled, string script_path);
      ```
* Changed:
  * Renamed `set_frame_content` to `set_frame`
  * Renamed `get_frame_content` to `get_frame`

## **0.4.1** - 2024-05-22

### Changed:
* Running Stipple Effect on Windows will now read and write settings from %appdata%.

## **0.4.0** - The Scripting Update - 2024-05-21

### Added:
* Added scripting
  * Added automation scripts
  * Added preview scripts
  * Added color scripts
* Added HSV level shifting
* Added "Flatten project" layer action
* Added themes
  * Added "Zo" theme inspired by Haitian culture and African diasporic religions
  * Added "Neon" theme
  * Added "Bunkering" theme inspired by the environmental degradation of the Niger Delta
* Added transparency checkerboard to the color slider core
* Dialog menus can now be closed by pressing *Enter* if the precondition is passing
* Added settings for windowed program size
* Added flag to include or exclude disabled layers in color actions

### Changed:
* Overhauled outlines
  * Outlines can now be internal as well as external and up to 10 pixels thick
  * Redesigned outlines dialog menu
* Optimization: "Project" menu elements are no longer redrawn after the canvas is edited

### Fixed:
* Bug: The dynamic label showing an example file name for the prefix and suffix doesn't update
* Bug: Lowest menu elements were clipped on the default settings page
* Bug: Not redrawing menus after a panel is shown/hidden in the panel manager dialog
* Bug: Setting pixel grid and checkerboard via shortcut (_Ctrl + B_) is resetting window size and theme

## **0.3.0** - The Settings and Options Update - 2024-04-08

### Added:
* Added text tool
* Added system timer
* Added tool options bar
* Added dither option to gradient tool
* Added second snap mode to pick up selection and move selection tools for pixel grid snapping
* Added stitching and splitting
* Added hotkeys and shortcuts:
  * *1* - *9* to quickly select one of the first 9 projects loaded into the program
  * Iterate through included palette colors (*Ctrl + ,* and *Ctrl + .*)
  * Set the checkerboard and grid cell size to the bounds of the selection/project (*Ctrl + B*)
* Added settings for:
  * Enabling/disabling state dumping
  * Direction inversion:
    * Zoom
    * Tool breadth incrementation (brush, eraser, shade brush, etc.)
    * Search tool tolerance incrementation (fill, wand)
    * Font size incrementation

### Changed:
* Redrew frame icons to feature purple film reels instead of orange motion blobs
* Changed box select tool's overlay to a tool content preview
* Modified box select tool snapping behaviour
* Modified polygon select behaviour: middle vertices can be placed off the canvas
* Changed brush select hotkey from *T* to *V*
* Changed pixel grid validation so that # of grid lines is checked instead of image bounds
* State stack is now dynamically sized; states are dumped based on insufficient available memory
* Redesigned and extended dialog menus
* Overhauled preview window
* Moved upcoming features from the top of the changelog to a dedicated "roadmap" tab
* Moved default frame prefix and suffix to "default" settings page and delete "format" page

### Fixed:
* Bug: Palettes were saved with the palette name instead of the filename
* Bug: Onion skin mode and layer settings buttons do not scroll with other layer menu elements
* Bug: Program sometimes crashes when launched with an image file or STIP project

## **0.2.0.1** - 2024-02-26

### Fixed:
* Crash bug that was caused by undo/redo operations not executing their consequence (e.g. undoing "add layer" operation did not redraw the layer buttons)

## **0.2.0** - 2024-02-16

### Added:
* Added tools:
  * Line tool
  * Gradient tool
  * Shade brush
  * Polygon select
* Extended TextBox to support text selection
* Added status updates for actions without visual feedback
  * Saving... 
  * Color slider adjustments and color actions when color panel is hidden
  * Layer actions when layer panel is hidden
  * Frame actions when frame panel is hidden
* Pixel grid
  * Holding Shift while using box select snaps selection to pixel grid
* New fonts SE (default) and Pencil SE
* Snap option for moving (translating) selection bounds and contents
### Changed:
* Redesigned order and layout of layer-specific buttons
* Refactored much of ToolWithBreadth to interface HasBreadth
* Optimized STIP files to save static layers as single frame
  * Incremented STIP file standard from 1.0 to 1.1
* Cropping to selection bounds now snaps the image to the center of the screen
* Independent checkerboard settings for x and y that can be set to values between 1-256
* Updated splash screen
* Optimized palettization of frame-linked layers 
* Merging an unlinked layer onto a frame-linked layer in a project with multiple frames will now result in an unlinked layer that preserves each frame's contents
* Optimized and refactored state management system
### Fixed:
* Bug: Pasting onto new layer does not trigger a re-draw of the layer panel menu
* Bug: Nonsensical cursor positions suggest rotate selection operation instead of move selection

## **0.1.0** - 2024-01-22

### Added:
* Added tooltips for icon buttons and icon toggle buttons
* Added palettes
  * Palette files (.stippal)
  * Palettization (scope: layer/frame/entire project)
* Added native project file type (.stip)
* Added preview window
* Added customizable panel layout
* Added exit program button and corresponding are-you-sure dialog
* Added snap angles for image rotation (activated by holding Shift; multiples of 45 degrees)
### Changed:
* New file dialog now suggests project canvas size based on clipboard image size if present
* Canvas size limit is now 800x800
* Updated program icon and logo animation
* Changed settings dialog to an info-style dialog with multiple tabs
* Reworked panels and render scale to be dependent on the window and screen size
* Changed brush select behaviour to preview overlay without editing selection until unclick
* Brush select contents are now filtered by canvas bounds
* Made list buttons dynamic; now only redraws menus when corresponding collection size changes
### Fixed:
* HSV slider slippage
* Optimized selection overlays (still suboptimal)
* Fixed bug where closing project window doesn't update menu
* Fill behaviour now replaces color rather than filling over
### Removed:
* Remove Piccolo font; set Classic to default

## **0.0.2** - 2024-01-06

### Added:
* Added vertical and horizontal selection reflection 
* Added selection stretching 
* Added selection rotation 
* Added MP4 exporting
### Changed:
* Updated program icon
### Fixed:
* Fixed bug that would crash the program when attempting to call getFileName() on the root of a Path 
* Fixed bug that would crash the program when performing an undo/redo operation that resized the canvas - was caused by those operations not redrawing the checkerboard transparency background

## **0.0.1.3** - 2023-12-25

### Added:
* Added Piccolo font and set as default
* Added outlining system
### Changed:
* Changed default additional layer name prefix to "Layer " from "Ly. "
### Fixed:
* Fixed bug that would cause eraser to malfunction on non-square canvases

## **0.0.1.2** - 2023-12-24

### Added:
* Added the ability to import multiple files at once
### Fixed:
* Rendering onion skins and semi-transparent layers is now significantly more efficient and performant
* Typing in a textbox now locks off keyboard shortcuts while the textbox is enabled
* Fixed gaps on pencil select tool, renamed to brush select, made into a selection equivalent of brush tool
### Changed:
* GIF file exports are now processed in a new thread, so the program can be used while the process is ongoing
* Improved zooming by adding limits to anchor area and no longer adjusting the anchor on zoom outs
* Added customizable suffixes for PNG_SEPARATE save mode and associated settings to set defaults
### Removed:
* Mini opacity slider in the layers panel; layer buttons are now bigger and show more of the layer name, and the opacity can still be modified from the layer settings dialog

## **0.0.1.1** - 2023-12-23
### Added:
* Status updates for:
  * Project saved
### Fixed:
* Picked up selection previews no longer shroud pixels on other layers
* Any picked up selection is dropped onto editing layer before attempting pasting 
* Contents of paste are now snapped to the bounds of the canvas
* Opening pad dialog after previous pad operation no longer preserves previous pad values in the previewed bounds calculation
* Saving a file will now drop any picked up selection contents back onto the layer before saving the file

## **0.0.1** - 2023-12-23
### Added:
* First available version of Stipple Effect
