<template>
  <button @click="openWidget()">Upload files</button>
</template>

<script setup lang="ts">
import {CLOUDINARY_CLOUD_NAME, CLOUDINARY_UPLOAD_PRESET} from "@/infrastructure/constants/cloudinary.ts";

const cloudName = CLOUDINARY_CLOUD_NAME;
const uploadPreset = CLOUDINARY_UPLOAD_PRESET;

const myWidget = cloudinary.createUploadWidget(
    {
      cloudName: cloudName,
      uploadPreset: uploadPreset,
    },
    (error, result) => {
      if (!error && result && result.event === "success") {
        console.log("Done! Here is the image info: ", result.info);
      }
    }
);

const openWidget = () => {
  myWidget.open();
}

</script>

