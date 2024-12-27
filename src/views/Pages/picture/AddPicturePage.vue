<template>
  <h2 style="margin-bottom: 16px">{{ route.query?.id ? '修改图片' : '创建图片' }}</h2>
  <div id="addPicturePage">
    <!-- 选择上传方式 -->
    <a-tabs v-model:activeKey="uploadType"
      >>
      <a-tab-pane key="file" tab="文件上传">
        <PictureUpload :picture="picture" :onSuccess="onSuccess" />
      </a-tab-pane>
      <a-tab-pane key="url" tab="URL 上传" force-render>
        <UrlPictureUpload :picture="picture" :onSuccess="onSuccess" />
      </a-tab-pane>
    </a-tabs>

    <a-form v-if="picture" layout="vertical" :model="pictureForm" @finish="handleSubmit">
      <a-form-item label="名称" name="name">
        <a-input v-model:value="pictureForm.name" placeholder="请输入名称" />
      </a-form-item>
      <a-form-item label="简介" name="introduction">
        <a-textarea
          v-model:value="pictureForm.introduction"
          placeholder="请输入简介"
          :auto-size="{ minRows: 2, maxRows: 5 }"
          allowClear
        />
      </a-form-item>
      <a-form-item label="分类" name="category">
        <a-auto-complete
          v-model:value="pictureForm.category"
          placeholder="请输入分类"
          allowClear
          :options="categoryOptions"
        />
      </a-form-item>
      <a-form-item label="标签" name="tags">
        <a-select
          v-model:value="pictureForm.tags"
          mode="tags"
          placeholder="请输入标签"
          allowClear
          :options="tagOptions"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">创建</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup>
import PictureUpload from '@/components/pictureUpload.vue'
import { onMounted, reactive, ref } from 'vue'
import { editPicture, getPictureVOById, listPictureTagCategory } from '@/myapi/picture'
import { message } from 'ant-design-vue'
// 路由
import { useRoute, useRouter } from 'vue-router'
import UrlPictureUpload from '@/components/UrlPictureUpload.vue'
const router = useRouter()
// 回调回来的图片
const picture = ref()
// 表单数据
const pictureForm = reactive({})
const onSuccess = (newvalue) => {
  picture.value = newvalue
  pictureForm.name = newvalue.name
}

// 提交 创建
const handleSubmit = async (value) => {
  const pictureId = picture.value?.id
  if (!pictureId) {
    return
  }
  const res = await editPicture({ id: pictureId, ...value })
  if (res.code == 0 && res.data) {
    message.success('创建成功')
    // 将上传成功的信息返回给父组件
    // props.onSuccess?.(res.data)
    router.push({
      path: `/picture/${pictureId}`,
    })
  } else {
    message.error('创建失败,' + res.message)
  }
}

const categoryOptions = ref([])
const tagOptions = ref([])

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategory()
  if (res.code === 0 && res.data) {
    // 转换成下拉选项组件接受的格式
    tagOptions.value = (res.data.tagList ?? []).map((data) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.categoryList ?? []).map((data) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('加载选项失败，' + res.data.message)
  }
}
// 进来的时候获取分类列表数据
onMounted(() => {
  getTagCategoryOptions()
})

const route = useRoute()

// 获取老数据（如果路由id有带的话，就拿到后端查，如果没有的话就不查）
const getOldPicture = async () => {
  // 获取数据
  const id = route.query?.id
  console.log(id)
  if (id) {
    const res = await getPictureVOById({
      id: id,
    })
    if (res.code === 0 && res.data) {
      const data = res.data
      picture.value = data
      pictureForm.name = data.name
      pictureForm.introduction = data.introduction
      pictureForm.category = data.category
      pictureForm.tags = data.tags
    }
  }
}

onMounted(() => {
  getOldPicture()
})
const uploadType = ref('file')
</script>
<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
