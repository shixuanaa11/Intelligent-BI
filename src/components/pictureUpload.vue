<template>
  <div class="picture-upload">
    <a-upload
      list-type="picture-card"
      :show-upload-list="false"
      :before-upload="beforeUpload"
      :customRequest="handleUpload"
    >
      <img v-if="props.picture?.url" :src="props.picture?.url" alt="avatar" />
      <div v-else>
        <loading-outlined v-if="loading"></loading-outlined>
        <plus-outlined v-else></plus-outlined>
        <div class="ant-upload-text">点击或拖拽上传图片</div>
      </div>
    </a-upload>
  </div>
</template>
<script setup>
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { ref } from 'vue'
import { uploadPicture } from '@/myapi/picture'

const props = defineProps({
  picture: {
    type: String,
  },
  onSuccess: {
    type: Function,
  },
})

const loading = ref(false)

/**
 * 上传图片
 * @param param0
 */
const handleUpload = async ({ file }) => {
  loading.value = true
  try {
    const formData = new FormData()
    const params = props.picture ? props.picture.id : ''
    formData.append('file', file)
    formData.append('id', params)
    const res = await uploadPicture(formData)

    if (res.code == 0 && res.data) {
      message.success('上传成功')
      // 将上传成功的信息返回给父组件
      props.onSuccess?.(res.data)
    } else {
      message.error('上传失败,' + res.message)
    }
  } catch (error) {
    message.error('error')
    message.error('上传失败' + error.message)
  }

  loading.value = false
}

/**
 *
 * @param file 上传文件前校验
 */
const beforeUpload = (file) => {
  // 校验图片格式
  const isJpgOrPng =
    file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/webp'
  if (!isJpgOrPng) {
    message.error('不支持该图片的格式，推荐 jpg 或 png')
  }
  // 校验图片大小
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('不能上传超过2M的图片')
  }
  return isJpgOrPng && isLt2M
}
</script>
<style scoped>
.picture-upload :deep(.ant-upload) {
  width: 100% !important;
  height: 100% !important;
  min-height: 152px;
  min-width: 152px;
}

.picture-upload img {
  max-width: 100%;
  max-height: 480px;
}

.ant-upload-select-picture-card i {
  font-size: 32px;
  color: #999;
}

.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}
</style>
