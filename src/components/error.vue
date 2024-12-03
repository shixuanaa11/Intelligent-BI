<template>
  <a-result status="404" title="404" sub-title="抱歉，该页面未找到">
    <template #extra>
      <a-button type="primary" @click="backHome">返回主页</a-button>
    </template>
  </a-result>
</template>
<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const backHome = () => {
  router.push('/home')
}
</script>
