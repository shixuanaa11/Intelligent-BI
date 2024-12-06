<template>
  个人页11
  <a-dropdown>
    <a class="ant-dropdown-link" @click.prevent>
      <EllipsisOutlined />
    </a>
    <template #overlay>
      <a-menu>
        <a-menu-item>
          <a href="javascript:;">1st menu item</a>
        </a-menu-item>
        <a-menu-item>
          <a href="javascript:;">2nd menu item</a>
        </a-menu-item>
        <a-menu-item>
          <a href="javascript:;">3rd menu item</a>
        </a-menu-item>
      </a-menu>
    </template>
  </a-dropdown>
</template>
<script setup>
import { EllipsisOutlined } from '@ant-design/icons-vue'
</script>
