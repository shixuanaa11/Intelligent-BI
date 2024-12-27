declare namespace API {
  type addParams = {
    name: string
  }

  type BaseResponseBiResponse = {
    code?: number
    data?: BiResponse
    message?: string
    description?: string
  }

  type BaseResponseBoolean = {
    code?: number
    data?: boolean
    message?: string
    description?: string
  }

  type BaseResponseInteger = {
    code?: number
    data?: number
    message?: string
    description?: string
  }

  type BaseResponseLoginUserVO = {
    code?: number
    data?: LoginUserVO
    message?: string
    description?: string
  }

  type BaseResponseLong = {
    code?: number
    data?: number
    message?: string
    description?: string
  }

  type BaseResponsePageChart = {
    code?: number
    data?: PageChart
    message?: string
    description?: string
  }

  type BaseResponsePagePicture = {
    code?: number
    data?: PagePicture
    message?: string
    description?: string
  }

  type BaseResponsePagePictureVO = {
    code?: number
    data?: PagePictureVO
    message?: string
    description?: string
  }

  type BaseResponsePicture = {
    code?: number
    data?: Picture
    message?: string
    description?: string
  }

  type BaseResponsePictureTagCategory = {
    code?: number
    data?: PictureTagCategory
    message?: string
    description?: string
  }

  type BaseResponsePictureVO = {
    code?: number
    data?: PictureVO
    message?: string
    description?: string
  }

  type BaseResponseString = {
    code?: number
    data?: string
    message?: string
    description?: string
  }

  type BiResponse = {
    aiGenChart?: string
    aiGenResult?: string
    chartId?: number
  }

  type Chart = {
    id?: number
    goal?: string
    chartName?: string
    chartData?: string
    chartType?: string
    aiGenChart?: string
    aiGenResult?: string
    userId?: number
    createTime?: string
    updateTime?: string
    status?: string
    execMessage?: string
    isDelete?: number
  }

  type ChartDeleteRequest = {
    id?: number
  }

  type ChartQueryRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    name?: string
    goal?: string
    chartType?: string
    userId?: number
  }

  type DeleteRequest = {
    id?: number
  }

  type genChartByAiAsyncMQParams = {
    genChartByAiRequest: GenChartByAiRequest
  }

  type genChartByAiAsyncParams = {
    genChartByAiRequest: GenChartByAiRequest
  }

  type genChartByAiParams = {
    genChartByAiRequest: GenChartByAiRequest
  }

  type GenChartByAiRequest = {
    goal?: string
    chartName?: string
    chartType?: string
  }

  type getPictureByIdParams = {
    id: number
  }

  type getPictureVOByIdParams = {
    id: number
  }

  type LoginUserVO = {
    id?: number
    account?: string
    username?: string
    avatarUrl?: string
    userRole?: string
    gender?: number
    createTime?: string
    updateTime?: string
  }

  type OrderItem = {
    column?: string
    asc?: boolean
  }

  type PageChart = {
    records?: Chart[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageChart
    searchCount?: PageChart
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type PagePicture = {
    records?: Picture[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PagePicture
    searchCount?: PagePicture
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type PagePictureVO = {
    records?: PictureVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PagePictureVO
    searchCount?: PagePictureVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type Picture = {
    id?: number
    url?: string
    name?: string
    introduction?: string
    category?: string
    tags?: string
    picSize?: number
    picWidth?: number
    picHeight?: number
    picScale?: number
    picFormat?: string
    userId?: number
    createTime?: string
    editTime?: string
    updateTime?: string
    isDelete?: number
  }

  type PictureEditRequest = {
    id?: number
    name?: string
    introduction?: string
    category?: string
    tags?: string[]
  }

  type PictureQueryRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    url?: string
    name?: string
    introduction?: string
    category?: string
    tags?: string[]
    picSize?: number
    picWidth?: number
    picHeight?: number
    picScale?: number
    picFormat?: string
    userId?: number
    searchText?: string
  }

  type PictureTagCategory = {
    categoryList?: string[]
    tagList?: string[]
  }

  type PictureUpdateRequest = {
    id?: number
    url?: string
    name?: string
    introduction?: string
    category?: string
    tags?: string[]
  }

  type PictureUploadRequest = {
    id?: number
  }

  type PictureVO = {
    id?: number
    url?: string
    name?: string
    introduction?: string
    tags?: string[]
    category?: string
    picSize?: number
    picWidth?: number
    picHeight?: number
    picScale?: number
    picFormat?: string
    userId?: number
    createTime?: string
    editTime?: string
    updateTime?: string
    user?: LoginUserVO
  }

  type testDownloadFile1Params = {
    filepath: string
  }

  type testDownloadFileParams = {
    filepath: string
  }

  type UploadFileParams = {
    pictureUploadRequest: PictureUploadRequest
  }

  type UserLoginRequest = {
    account?: string
    password?: string
  }

  type UserRegisterRequest = {
    account?: string
    password?: string
    checkPassword?: string
  }
}
