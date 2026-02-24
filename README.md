# volcal_baseline_android

An Android client for interacting with the [`volcal_baseline`](https://github.com/deemoe404/volcal_baseline) pipeline via the [`volcal_baseline_server`](https://github.com/deemoe404/volcal_baseline_server) web service.  
The `volcal_baseline` system provides a **data-driven pipeline for large-scale open-pit mine excavation monitoring** based on multi-temporal 3D point clouds.

This mobile application enables field users to submit point cloud data, monitor processing progress, and review excavation change results—**directly from Android embedded devices**, including DJI remote controllers and other edge-computing platforms.

---

## 📱 Key Features

- **Resumable File Uploads**  
  Upload the reference point cloud, changed point cloud, and stable-area shapefile using the TUS protocol.
  
- **Task Management**  
  Create new tasks, monitor their processing status, and view a history of completed jobs.

- **Visual Feedback**  
  Inspect computed convex hulls, cut/fill volumes, and associated images after server-side processing is complete.

For more technical details, refer to the respective upstream repositories.

---

## 🔧 Server Address Configuration

The app base URL is configurable through a Gradle property:

```bash
./gradlew assembleDebug -PAPI_BASE_URL=http://192.168.1.26:8000/
```

If omitted, the default value is `http://192.168.1.26:8000/`.

For end-to-end success with `volcal_baseline_server`, keep these three endpoints on the
same reachable host/IP:

- Android `API_BASE_URL`: `http://<HOST>:8000/`
- Server `TUS_ENDPOINT`: `http://<HOST>:1080/files`
- Server `MINIO_ENDPOINT`: `http://<HOST>:9000`

---

## 📦 License

This project is released under the [MIT License](./LICENSE).
