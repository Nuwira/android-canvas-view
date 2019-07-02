# android-canvas-view
A simple Android canvas view

## Download [![](https://jitpack.io/v/Nuwira/android-canvas-view.svg)](https://jitpack.io/#Nuwira/android-canvas-view)

Add it in your root `build.gradle`

```groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

And add the dependency

```groovy
dependencies {
	implementation 'com.github.Nuwira:android-canvas-view:$latestVersion'
}
```

## Usage
```xml
<id.co.nuwira.canvasview.CanvasView
        android:id="@+id/canvas_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:cv_stroke_width="20"                        // default 6
        app:cv_stroke_color="@color/colorPrimary"       // default black
        app:cv_background_color="@android:color/white"  // default white
        app:cv_hint="Sign here"                         // optional
        app:cv_hint_color="@android:color/darker_gray"  // default gray
        app:cv_hint_size="10sp"                         // default 12
        app:cv_hint_visibility="visible"/>              // default visible
```

## Clear Canvas
```java
  canvasView.clearCanvas();
```

## Get Bitmap
```java
  canvasView.getBitmap();
```
