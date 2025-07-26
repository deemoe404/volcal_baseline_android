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

## 📦 License

This project is released under the [MIT License](./LICENSE).
