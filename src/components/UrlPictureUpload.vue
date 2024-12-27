<template>
  <div class="url-picture-upload">
    <a-input-group compact style="margin-bottom: 16px">
      <a-input
        v-model:value="fileUrl"
        style="width: calc(100% - 120px)"
        placeholder="请输入图片 URL"
      />
      <a-button type="primary" :loading="loading" @click="handleUpload" style="width: 120px"
        >提交</a-button
      >
    </a-input-group>
    <a-flex justify="center">
      <a-image v-if="picture?.url" :src="picture?.url" alt="avatar" />
    </a-flex>
  </div>
</template>
<script setup>
import { uploadPictureByUrl } from '@/myapi/picture'
import { message } from 'ant-design-vue'
import { ref } from 'vue'

const props = defineProps({
  picture: {
    type: String,
  },
  onSuccess: {
    type: Function,
  },
})
// 图片的url
const fileUrl = ref()
// 加载按钮
const loading = ref(false)
/**
 * 上传图片
 * @param param0
 */
const handleUpload = async () => {
  loading.value = true
  try {
    const params = { fileUrl: fileUrl.value }
    // 如果传进来有图片对象，就把图片id加上去修改这张图片
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrl(params)

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
</script>
<style scoped>
.url-picture-upload {
  margin-bottom: 16px;
}

.url-picture-upload img {
  max-width: 100%;
  max-height: 480px;
}
.url-picture-upload .img-wrapper {
  text-align: center;
  margin-top: 16px;
}
</style>
