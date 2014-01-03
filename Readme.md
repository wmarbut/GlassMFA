# Multifactor Auth Token Generator for Glass

# License

GlassMFA is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.

http://creativecommons.org/licenses/by-nc-sa/4.0/

# Pre-Built APK

You can find them in the 'releases' directory

# TOTP to QR utility

I've written a simple utility to create a QR code from your existing TOTP secrets into something that Glass can consume. You can find it in *qr-creator* directory. It is compiled for every major platform.

Here's how to use it.

`totp-qr <label> <secret> <output_file_path_for_png>`
