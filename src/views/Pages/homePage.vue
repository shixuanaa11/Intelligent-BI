<template>
  <div id="home-page">
    <!-- 搜索框 -->
    <div class="search-bar">
      <a-input-search
        placeholder="从海量图片中搜索"
        v-model:value="searchParams.searchText"
        enter-button="搜索"
        size="large"
        @search="doSearch"
      />
    </div>
    <!-- 分类 -->
    <a-tabs v-model:activeKey="categoryKey" @change="doSearch">
      <a-tab-pane key="all" tab="全部"></a-tab-pane>
      <a-tab-pane v-for="category in categoryOptions" :key="category" :tab="category"></a-tab-pane>
    </a-tabs>
    <!-- 标签 -->
    <div class="tags-bar">
      <span style="margin-right: 8px">标签:</span>
      <a-space :size="[0, 8]" wrap>
        <a-checkable-tag
          v-for="(tag, index) in tagOptions"
          :key="tag"
          v-model:checked="SelectTags[index]"
          @change="doSearch"
        >
          {{ tag }}
        </a-checkable-tag>
      </a-space>
    </div>
    <!-- 图片列表 -->
    <a-list
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 4, lg: 4, xl: 6 }"
      :data-source="data"
      :pagination="pagination"
      :loading="loading"
    >
      <template #renderItem="{ item: picture }">
        <a-list-item style="padding: 0">
          <!-- 单张图片 -->
          <a-card hoverable @click="doclickPicture(picture)">
            <template #cover>
              <img
                style="height: 180px; object-fit: cover"
                :alt="picture.name"
                :src="picture.thumbnailUrl"
              />
            </template>
            <a-card-meta :title="picture.name">
              <template #description>
                <a-flex>
                  <a-tag color="green">
                    {{ picture.category ?? '默认' }}
                  </a-tag>
                  <a-tag v-for="tag in picture.tags" :key="tag">
                    {{ tag }}
                  </a-tag>
                </a-flex>
              </template>
            </a-card-meta>
          </a-card>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>
<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { listPictureTagCategory, listPictureVOByPage } from '@/myapi/picture'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
const data = ref([])
const loading = ref(false)
// const total = ref()

// 搜索条件
const searchParams = reactive({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
})
const total = ref(0) // 用于存储总条目数
// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    // showSizeChanger: true,
    // showTotal: (total) => `共 ${total} 条`,
    onChange: (page, pageSize) => {
      searchParams.current = page
      searchParams.pageSize = pageSize
      fetchData()
    },
  }
})
// 获取数据
const fetchData = async () => {
  loading.value = true
  // 分类标签的搜索条件判断
  const params = {
    ...searchParams,
    tags: [],
  }
  // 分类判断，如果不为all就给他加条件
  if (categoryKey.value !== 'all') {
    params.category = categoryKey.value
  }
  // 标签：[false,true,false...]=>['标签1','标签2']
  console.log(SelectTags.value)
  SelectTags.value.forEach((useTag, index) => {
    if (useTag) {
      params.tags.push(tagOptions.value[index])
    }
  })
  console.log(params.tags)

  const res = await listPictureVOByPage(params)
  if (res.data) {
    data.value = res.data.records ?? []
    total.value = res.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
  loading.value = false
}
onMounted(() => {
  fetchData()
})
// 搜索
const doSearch = () => {
  // 重置搜索条件
  searchParams.current = 1
  fetchData()
}
// 获取标签和分类选项
const tagOptions = ref([])
const categoryOptions = ref([])
const categoryKey = ref('all')
const SelectTags = ref([])
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategory()
  console.log(res)

  if (res.code === 0 && res.data) {
    // 转换成下拉选项组件接受的格式
    tagOptions.value = res.data.tagList ?? []
    categoryOptions.value = res.data.categoryList ?? []
  } else {
    message.error('加载选项失败，' + res.data.message)
  }
}
// 进来的时候获取分类列表数据
onMounted(() => {
  getTagCategoryOptions()
})
// 卡片点击
// 路由
const router = useRouter()
const doclickPicture = (picture) => {
  // 跳转到图片详情页
  router.push({
    path: `picture/${picture.id}`,
  })
}
</script>
<style scoped>
#home-page .search-bar {
  max-width: 480px;
  margin: 0 auto 16px;
}
#home-page .tags-bar {
  margin-bottom: 20px;
}
</style>
