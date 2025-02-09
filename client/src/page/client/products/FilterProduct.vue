<template>
  <div>
    <h1 class="text-base font-bold">Bạn đã chọn</h1>
    <hr />
    <div>
      <a-collapse v-model:activeKey="activeKey" accordion :bordered="false">
        <a-collapse-panel key="1" header="Màu sắc">
          <p>{{ text }}</p>
        </a-collapse-panel>
      </a-collapse>
    </div>
    <div>
      <a-collapse v-model:activeKey="activeKey" accordion :bordered="false">
        <a-collapse-panel key="1" header="Kích cỡ">
          <p>{{ text }}</p>
        </a-collapse-panel>
      </a-collapse>
    </div>
    <div>
      <a-collapse v-model:activeKey="activeKey" accordion :bordered="false">
        <a-collapse-panel key="1" header="Danh mục">
          <p>{{ text }}</p>
        </a-collapse-panel>
      </a-collapse>
    </div>
    <div>
      <a-collapse v-model:activeKey="activeKey" accordion :bordered="false">
        <a-collapse-panel key="1" header="Đối tượng">
          <p>{{ text }}</p>
        </a-collapse-panel>
      </a-collapse>
    </div>
  </div>
</template>
<script lang="ts" setup>
</script>
