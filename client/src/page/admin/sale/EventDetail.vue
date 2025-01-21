<template>
    <a-timeline>
      <a-timeline-item color="red">
        <p>Solve initial network problems 1</p>
        <p>Technical testing 2</p>
      </a-timeline-item>
      <a-timeline-item>
        <p>Technical testing 1</p>
        <p>Technical testing 2</p>
      </a-timeline-item>
      <a-timeline-item color="">
        <p>Technical testing 1</p>
        <p>Technical testing 3 2015-09-01</p>
      </a-timeline-item>
      <a-timeline-item color="">
        <p>Technical testing 1</p>
        <p>Technical testing 2</p>
      </a-timeline-item>
      <a-timeline-item color="">
        <template #dot>
          <SmileOutlined />
        </template>
        <p>Custom color testing</p>
        <p>Technical testing 1</p>

      </a-timeline-item>
    </a-timeline>
  </template>



  <script lang="ts" setup>
  import { SmileOutlined } from '@ant-design/icons-vue';
  </script>
  
  