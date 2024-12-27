<template>
  <div id="pictureDetailPage">
    <a-row :gutter="[16, 16]">
      <!-- 图片展示区 -->
      <a-col :sm="24" :md="16" :xl="17">
        <a-card title="图片预览">
          <a-image style="max-height: 600px; object-fit: contain" :src="picture.url" />
        </a-card>
      </a-col>
      <!-- 图片信息区 -->
      <a-col :sm="24" :md="8" :xl="7">
        <a-card title="图片信息">
          <a-descriptions :column="1">
            <a-descriptions-item label="作者">
              <a-space>
                <a-avatar :size="24" :src="picture.user?.avatarUrl" />
                <div>
                  {{ picture.user?.userName ? picture.user?.userName : picture.user?.account }}
                </div>
              </a-space>
            </a-descriptions-item>
            <a-descriptions-item label="名称">
              {{ picture.name ?? '未命名' }}
            </a-descriptions-item>
            <a-descriptions-item label="简介">
              {{ picture.introduction ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="分类">
              {{ picture.category ?? '默认' }}
            </a-descriptions-item>
            <a-descriptions-item label="标签">
              <a-tag v-for="tag in picture.tags" :key="tag">
                {{ tag }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="格式">
              {{ picture.picFormat ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽度">
              {{ picture.picWidth ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="高度">
              {{ picture.picHeight ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽高比">
              {{ picture.picScale ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="大小">
              {{ formatSize(picture.picSize) }}
            </a-descriptions-item>
          </a-descriptions>
          <a-space wrap>
            <a-button type="primary" @click="doDownload">
              <template #icon>
                <DownloadOutlined />
              </template>
              免费下载
            </a-button>
            <a-button v-if="canEdit" type="default" @click="doEdit">
              编辑
              <template #icon>
                <EditOutlined />
              </template>
            </a-button>
            <a-button v-if="canEdit" danger @click="doDelete">
              删除
              <template #icon>
                <DeleteOutlined />
              </template>
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>
<script setup>
import { deletePicture, getPictureVOById } from '@/myapi/picture'
import { downloadImage, formatSize } from '@/utils'
import { message, Modal } from 'ant-design-vue'
import { EditOutlined, DeleteOutlined, DownloadOutlined } from '@ant-design/icons-vue'
import { computed, onMounted, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const props = defineProps(['id'])
onMounted(() => {
  console.log(props.id)
})

// 获取老数据（如果路由id有带的话，就拿到后端查，如果没有的话就不查）
const picture = ref({})
const getOldPicture = async () => {
  // 获取数据
  // console.log(id)
  try {
    const res = await getPictureVOById({
      id: props.id,
    })
    if (res.code === 0 && res.data) {
      picture.value = res.data
    } else {
      message.error('获取图片失败,', res.message)
    }
  } catch (error) {
    message.error('获取图片失败,', error.message)
    console.log(error)
  }
}

onMounted(() => {
  getOldPicture()
})
// 动态控制按钮的显隐，只有管理员或者用户本人才显示按钮
const canEdit = computed(() => {
  const loginUser = useUserStore().userInfo
  console.log(loginUser.id)

  // 未登录不可编辑
  if (!loginUser.id) {
    return false
  }
  // 只有本人或者管理员才可以编辑
  const user = picture.value.user || {}
  return loginUser.id === user.id || loginUser.userRole === 'admin'
})
// 编辑
const router = useRouter()
const doEdit = () => {
  console.log('编辑')
  router.push('/add_picture/?id=' + picture.value.id)
}
// 删除
const doDelete = async () => {
  Modal.confirm({
    title: '删除图片',
    // icon: h(ExclamationCircleOutlined),
    content: '你确定要删除该图片?',
    okText: '确认',
    cancelText: '取消',
    async onOk() {
      try {
        const res = await deletePicture({ id: picture.value.id })
        if (res.code == 0) {
          message.success('删除成功')
          router.push({
            path: '/',
          })
        } else {
          message.error('删除失败')
        }
      } catch {
        return console.log('Oops errors!')
      }
    },
    onCancel() {},
  })

  console.log('删除')
}
// 下载
const doDownload = () => {
  downloadImage(picture.value.url)
  console.log('下载')
}
</script>
<style scoped>
#pictureDetailPage {
  margin-bottom: 16px;
}
</style>
