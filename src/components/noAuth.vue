<template>
  <a-result status="403" title="403" sub-title="抱歉，你没有该页面的访问权限">
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
