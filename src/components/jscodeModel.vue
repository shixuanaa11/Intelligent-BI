<template>
  <a-modal v-model:open="open" title="JS代码" @ok="handleOk" :width="1000" :footer="null">
    <CodeEditor :value="data"></CodeEditor>
  </a-modal>
</template>
<script setup>
import { onMounted, ref } from 'vue'
import CodeEditor from './codeEditor.vue'

const open = ref(false)

const handleOk = () => {
  console.log('ok')
}

onMounted(() => {
  // console.log('传进来的', props.jsdata)
})
const data = ref()
// 接收数据
const getdata = (item) => {
  data.value = item
  console.log('data', data.value)
}
// 打开弹窗
const openModal = () => {
  open.value = true
}
// 关闭弹窗
const exitModel = () => {
  open.value = false
}
defineExpose({
  openModal,
  exitModel,
  getdata,
})
</script>
