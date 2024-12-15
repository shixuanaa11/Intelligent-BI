<template>
  <div ref="editorContainer" id="editorContainer" style="height: 400px"></div>
</template>

<script>
import * as monaco from 'monaco-editor'
import { ref, onMounted } from 'vue'

export default {
  props: {
    data: String,
  },
  setup(props) {
    const editor = ref(null)
    const data = ref('helloworld')

    onMounted(() => {
      editor.value = monaco.editor.create(document.getElementById('editorContainer'), {
        value: data.value,
        language: props.language || 'javascript',
        minimap: {
          enabled: true,
        },
        colorDecorators: true, //颜色装饰器
        readOnly: false, //是否开启已读功能
        theme: 'vs-dark', //主题
      })

      // 监听编辑器内容变化
    })
    // editor.value.onDidChangeModelContent(() => {
    //   // 触发父组件的 change 事件，通知编辑器内容变化
    //   props.onChange(editor.value.getValue())
    // })

    return {
      editor,
    }
  },
}
</script>
