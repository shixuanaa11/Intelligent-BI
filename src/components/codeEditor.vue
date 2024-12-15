<template>
  <div id="code-editor" ref="codeEditorRef" style="min-height: 500px"></div>
  <!--  <a-button @click="fillValue"></a-button>-->
</template>
<script setup lang="ts">
import * as monaco from 'monaco-editor'
import { defineProps, onMounted, ref, toRaw, watch, withDefaults } from 'vue'

interface Props {
  value: object
  handleChange: (v: string) => void
}

const props = withDefaults(defineProps<Props>(), {
  value: () => ({}),
  handleChange: (v: string) => {
    console.log(v)
  },
})
const codeEditorRef = ref()
const codeEditor = ref()
const fillValue = () => {
  if (!codeEditor.value) return
  toRaw(codeEditor.value).setValue('新的值')
}
onMounted(() => {
  if (!codeEditorRef.value) {
    return
  }
  // Hover on each property to see its docs!
  codeEditor.value = monaco.editor.create(codeEditorRef.value, {
    value:
      'option =' +
      JSON.stringify(props.value)
        .replace(/"([^"]+)":/g, '$1:') // 去掉键的引号
        .replace(/"/g, "'"), // 将双引号换成单引号,
    language: 'javascript',
    automaticLayout: true,
    minimap: {
      enabled: true,
    },
    // lineNumbers: "off",
    // roundedSelection: false,
    // scrollBeyondLastLine: false,
    readOnly: true,
    theme: 'vs-dark',
    overviewRulerBorder: false, // 滚动是否有边框
    formatOnPaste: true, //设置是否在粘贴文本时自动格式化代码
    mouseWheelZoom: true, //设置是否开启鼠标滚轮缩放功能
    folding: true, //控制是否开启代码折叠功能
  })
  // 自动格式化代码
  codeEditor.value.getAction('editor.action.formatDocument').run()
  // 编辑监听内容变化
  // codeEditor.value.onDidChangeModelContent(() => {
  //   props.handleChange(toRaw(codeEditor.value).getValue())
  // })
  // watch(
  //   () => props.value,
  //   (newValue) => {
  //     const formattedValue = `option =${JSON.stringify(newValue, null, 2)}` // 使用 `null, 2` 格式化 JSON
  //       .replace(/"([^"]+)":/g, '$1:') // 去掉键的引号
  //       .replace(/"/g, "'") // 将双引号换成单引号

  //     if (codeEditor.value?.getValue() !== formattedValue) {
  //       codeEditor.value?.setValue(formattedValue)
  //     }
  //   },
  // )
})
</script>

<style scoped></style>
