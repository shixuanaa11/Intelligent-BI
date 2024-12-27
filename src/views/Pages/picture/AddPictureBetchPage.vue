<template>
  <h2 style="margin-bottom: 16px">批量创建</h2>
  <div id="addPictureBatchPage">
    <a-form layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item label="关键词" name="searchText">
        <a-input v-model:value="formData.searchText" placeholder="请输入关键词" />
      </a-form-item>
      <a-form-item label="抓取数量" name="count">
        <a-input-number
          v-model:value="formData.count"
          placeholder="请输入数量"
          :min="1"
          :max="30"
          allowClear
          style="min-width: 180px"
        />
      </a-form-item>
      <a-form-item label="名称前缀" name="namePrefix">
        <a-input v-model:value="formData.namePrefix" placeholder="请输入名称前缀" />
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading"
          >执行任务</a-button
        >
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup>
import { reactive, ref } from 'vue'
// import { editPicture, getPictureVOById, listPictureTagCategory } from '@/myapi/picture'
import { message } from 'ant-design-vue'
// 路由
import { useRouter } from 'vue-router'
import { uploadPictureByBatch } from '@/myapi/picture'
const router = useRouter()
// const route = useRoute()
// 回调回来的图片
// const picture = ref()
// 表单数据
const formData = reactive({})
// 加载按钮
const loading = ref(false)
// 提交 创建
const handleSubmit = async () => {
  loading.value = true

  const res = await uploadPictureByBatch({ ...formData })
  if (res.code == 0 && res.data) {
    message.success(`创建成功 , 共${res.data}条`)
    // 将上传成功的信息返回给父组件
    // props.onSuccess?.(res.data)
    router.push({
      path: '/home',
    })
  } else {
    message.error('创建失败,' + res.message)
  }
  loading.value = false
}
</script>
<style scoped>
#addPictureBatchPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
