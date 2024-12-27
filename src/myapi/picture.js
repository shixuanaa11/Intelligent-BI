// 图片接口
import request from '@/utils/myaxios'
// 上传图片
export const uploadPicture = (data) =>
  request.post('/picture/upload', data, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
// 上传图片(URL)
export const uploadPictureByUrl = (url) => request.post('/picture/upload/url', url)
// 修改图片
export const editPicture = (data) => request.post('/picture/edit', data)
// 获取通用标签列表
export const listPictureTagCategory = () => request.get('/picture/tag_category')
// 根据 id 获取图片（封装类）
export const getPictureVOById = (id) => request.get('/picture/get/vo', { params: id })
// 分页获取图片列表信息(用户)
export const listPictureVOByPage = (data) => request.post('/picture/list/page/vo', data)
// 图片删除
export const deletePicture = (id) => request.post('/picture/delete', id)
// 获取全部图片信息(管理员)
export const listPictureByPage = (data) => request.post('/picture/list/page', data)
// 审核接口（管理员）
export const doPictureReview = (data) => request.post('/picture/review', data)
// 爬取图片（管理员，后面完善了可以开放给用户，不过有限制）
export const uploadPictureByBatch = (data) => request.post('/picture/upload/batch', data)
