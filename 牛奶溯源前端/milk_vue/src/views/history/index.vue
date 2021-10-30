<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="queryID" placeholder="请输入奶牛ID编号" style="width: 300px;" class="filter-item" @keyup.enter.native="queryInfo" >
        <el-button slot="suffix" icon="el-icon-close" type="text" circle @click="resetQueryInfo"></el-button>
      </el-input>

      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="queryInfo">
        搜索
      </el-button>

    </div>

    <el-table
      :data="list"
      border
      stripe
      style="width: 60%"
      :cell-style="{ textAlign: 'center' }"
      :header-cell-style="headClass"
    >
      <el-table-column
        prop="cowId"
        label="奶牛ID"
      />
      <el-table-column
        prop="operateTime"
        label="操作时间"
        width="300px"
      />
      <el-table-column
        prop="operation"
        label="操作项目"
      />
      <el-table-column
        prop="consumptionOrOutput"
        label="产出/消耗"
      />

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />


    <el-dialog :visible.sync="dialogPvVisible" title="Reading statistics">
      <el-table :data="pvData" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="key" label="Channel" />
        <el-table-column prop="pv" label="Pv" />
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogPvVisible = false">Confirm</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList, fetchQueryList } from '@/api/history'
import waves from '@/directive/waves' // waves directive
import { parseTime } from '@/utils'
import Pagination from '@/components/Pagination'

const calendarTypeOptions = [
  { key: 'CN', display_name: 'China' },
  { key: 'US', display_name: 'USA' },
  { key: 'JP', display_name: 'Japan' },
  { key: 'EU', display_name: 'Eurozone' }
]

// arr to obj, such as { CN : "China", US : "USA" }
const calendarTypeKeyValue = calendarTypeOptions.reduce((acc, cur) => {
  acc[cur.key] = cur.display_name
  return acc
}, {})

export default {
  name: 'ComplexTable',
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'info',
        deleted: 'danger'
      }
      return statusMap[status]
    },
    typeFilter(type) {
      return calendarTypeKeyValue[type]
    }
  },
  data() {
    return {
      queryID: '',
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        importance: undefined,
        title: undefined,
        type: undefined
      },
      importanceOptions: [1, 2, 3],
      calendarTypeOptions,
      statusOptions: ['published', 'draft', 'deleted'],
      showReviewer: false,
      temp: {
        cowId: '',
        operateTime: '',
        operation: '',
        consumptionOutput: ''
      },
      dialogFormVisible: false,
      dialogStatus: '',
      dialogPvVisible: false,
      pvData: [],
      downloadLoading: false,
      timeDefault: new Date(),
    }
  },
  created() {
    this.getList()
  },
  methods: {
    resetList() {
      this.list = null
    },
    getList() {
      fetchList(this.listQuery).then(response => {
        this.list = response.data.items
        this.total = response.data.total

        // Just to simulate the time of the request
        setTimeout(() => {
          this.listLoading = false
        }, 1.5 * 1000)
      })
    },
    queryInfo() {
      this.resetList()
      this.total = 0
      fetchQueryList(this.queryID).then(response => {
        if (response.code === 500) {
          this.$message({
            message: response.message,
            type: 'warning'
          })
        } else {
          console.log(response)
          this.list = response.data.items
          this.total = response.data.total
        }
      })
    },
    resetQueryInfo() {
      this.queryID = ''
      this.getList()
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleModifyStatus(row, status) {
      this.$message({
        message: '操作Success',
        type: 'success'
      })
      row.status = status
    },
    resetTemp() {
      this.temp = {
        cowId: '',
        operateTime: '',
        operation: '',
        consumptionOutput: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },

    handleUpdate(row) {
      this.temp = Object.assign({}, row) // copy obj
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          tempData.timestamp = +new Date(tempData.timestamp) // change Thu Nov 30 2017 16:41:05 GMT+0800 (CST) to 1512031311464
          updateUser(tempData).then(() => {
            const index = this.list.findIndex(v => v.id === this.temp.id)
            this.list.splice(index, 1, this.temp)
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '用户信息更新成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      const tempData = Object.assign({}, row) // copy obj
      const index = this.list.findIndex(v => v.id === tempData.id)
      this.list.splice(index, 1)
      deleteUser(tempData).then(() => {
        this.$notify({
          title: '成功',
          message: '用户删除成功',
          type: 'success',
          duration: 2000
        })
      }
      )
    },
    handleFetchPv(pv) {
      fetchPv(pv).then(response => {
        this.pvData = response.data.pvData
        this.dialogPvVisible = true
      })
    },
    formatJson(filterVal) {
      return this.list.map(v => filterVal.map(j => {
        if (j === 'timestamp') {
          return parseTime(v[j])
        } else {
          return v[j]
        }
      }))
    },
    getSortClass: function(key) {
      const sort = this.listQuery.sort
      return sort === `+${key}` ? 'ascending' : 'descending'
    },
    headClass() { // 表头居中显示
      return 'text-align:center'
    }
  }
}
</script>
