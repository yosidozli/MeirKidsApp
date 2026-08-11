# App signing: current status

There is no `keystore.jks`/`keystore.properties` in this repo. Release builds
compile unsigned unless a `keystore.properties` file is placed at the repo root
(`app/build.gradle` picks it up automatically when present — see the
`hasSigningConfig` guard). That file must never be committed; it's in
`.gitignore`.

## Good news: the original key is recoverable

`app/app-release.apk` (committed in this repo) is signed with the original
2017 release key. Verified with:

```
keytool -printcert -jarfile app/app-release.apk
```

```
Owner/Issuer: CN=yosef dozli, OU=dira, O=machon meir, L=rishon le zion 31, ST=israel, C=IL
Serial number: 2b0e4e1e
Valid from: 2017-07-02  until: 2042-06-26
Signature algorithm: SHA256withRSA, 2048-bit RSA
SHA1:   33:9E:6C:59:BA:15:27:A3:1D:C9:3D:FD:BF:A6:AE:62:84:DE:DC:CE
SHA256: 11:68:50:28:90:F5:92:EF:35:3E:99:9C:2A:B9:22:CB:17:C7:9E:6D:B4:FB:FA:4B:0C:FE:42:2C:78:63:3E:B0
```

## What to check before shipping (Play Console → App integrity → App signing)

1. **Two certificates listed** (an "App signing key certificate" and an
   "Upload key certificate") → Play App Signing is enrolled. Confirm the SHA-1
   above matches the *upload* certificate. If the upload key is genuinely
   lost, Play support can reset it — typically resolved within a few days,
   and does not put the app signing key at risk.
2. **One certificate**, matching the SHA-1 above → that certificate *is* the
   app signing key, and the original `.jks` file is required to upload a
   normal update. The remaining path is a Play Console key-upgrade / account
   recovery request (slower, and Google reviews it).

Either way, **this blocks shipping only** — it has no bearing on building,
installing, or testing the app locally with the debug key.

## Also confirm the live `versionCode`

`app/build.gradle` currently sets `versionCode 14`. The repo's own history
isn't authoritative about what actually shipped to users — check the live
value in Play Console and bump `versionCode` above it if it's already higher.
