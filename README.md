## OTP Verification with Jetpack Compose

This repository contains a custom-built OTP (One Time Password) Input Field and integration with Google's SMS Retriever API
for OTP fetch and populate functionality, developed using Jetpack Compose. This component is designed to cater to the needs
of modern Android applications requiring OTP verification, offering a blend of customization and ease-of-use.

![Compose](https://img.shields.io/badge/Compose_BOM-1.5.4-blue.svg?color=blue&style=for-the-badge)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg?color=blue&style=for-the-badge)

> This is not a library. This is a demo project to experiment and try out a functionality.

### Screenshots

<table style="width:100%">
  <tr>
    <th>OTP Verification</th>
    <th>OTP Screen UI</th>
  </tr>
  <tr>
    <td><img src = "art/screens/otp_verification.gif" width="100%" alt="OTP Verification"/></td>
    <td><img src = "art/screens/otp_ui.png" width="820" alt="OTP Screen UI"/></td>
  </tr>
</table>

### Features
- Integration with Google's SMS Retriever API: Automatically verify OTP using SMS Retriever API and populate in UI.
- Configurable Length: The OTP input field can be set up for any length of OTP, making it versatile for different verification scenarios.
- Automatic Population: It supports automatic filling of the OTP, a convenient feature for OTPs received from servers or other sources.
- Cursor Visibility and Blinking: The field offers options to show a cursor, and control its blinking, enhancing the user experience.

### Find this project useful ? ❤️

- Support it by clicking the ⭐️ button on the upper right of this page. ✌️

### License

```
MIT License

Copyright (c) 2024 Pushpal Roy

Permission is hereby granted, free of charge, to any person obtaining a 
copy of this software and associated documentation files (the "Software"), 
to deal in the Software without restriction, including without limitation 
the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the 
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included 
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```