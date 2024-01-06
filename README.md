# Architecture

This project contains some common features and utilities that can help develop app faster and easier. Here are some of
the things it handles by default:

- **Error handling**: The project has a `handleError` method that shows a snackbar with error details whenever an
  exception occurs while doing a request. This prevents the app from crashing and informs the user about the problem.
  All errors are also logged to Firebase Crashlytics with the `handleError` key.
- **Logging with firebase**: The project has `Throwable.logToFirebase` and `runCatchingLogToFirebase` which logs the
  error to firebase in case of exception.
- **No internet connection**: If there is no internet connection available, the `handleError` method shows a no internet
  screen with a button to refresh the data. To make this work on all screens, you need to use the `refresh` method
  provided by the `BaseViewModel` class properly. This method is used to refresh data in the viewModel after user clicks
  on the update button in the no internet screen. It is recommended to call this method for all use cases that request
  data from the remote server, and do it in the init block like this:
    ```kotlin
    init {
      refresh(false/true)
    }
    ```
  This ensures that when this method is called after internet loss, it will request all data again. You can also pass an
  optional parameter `isUpdateAll` to control whether to refresh all data or exclude some data from being refreshed. Set
  it to false if you want to skip refreshing some data when calling this method from the viewModel init block.
- **Shake to show requests**: The project uses [Chucker](https://www.markdownguide.org/tools/obsidian/), a library that
  allows to inspect HTTP requests and responses. You can access the Chucker UI by shaking your device while the app is
  running. This can help debug network issues and see what data is being sent and received.
- **Better error logs**: Base app error classes are now `data class` which means in stacktraces the content of error
  will appear.
- **Full screen support**: The project supports full screen mode by default, meaning it hides the status bar and
  navigation bar. If you want to change the screen mode for a particular screen, you can use the `screenMode` property
  in the `BaseFragment` class.
- **Status bar color support**: The project supports changing the status bar color for each screen. You can use
  the `statusBarMode` property in the `BaseFragment` class to set the desired color.
- **Telegram bot integration**: The project has updated gradle tasks that send apk via telegram bot. You can find more
  details about how to use this feature in the app gradle file.
