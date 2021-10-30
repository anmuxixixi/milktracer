<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.title" placeholder="请输入搜索内容" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />

      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate()">
        添加
      </el-button>

    </div>

    <el-table
      :data="list"
      border
      stripe
      style="width: 70%"
      :cell-style="{ textAlign: 'center' }"
      :header-cell-style="headClass"
    >
      <el-table-column
        prop="id"
        label="id"
        width="100px"
      />
      <el-table-column
        prop="name"
        label="奶牛场名"
      />
      <el-table-column
        prop="create_time"
        label="创建时间"
        width="300px"
      />
      <el-table-column
        prop="max_Cow_No"
        label="奶牛数量"
        width="150px"
      />
      <el-table-column
        prop="max_Bucket_No"
        label="奶桶数量"
        width="150px"
      />
      <!-- row表示这行的信息，index表示这行的id -->
      <el-table-column label="操作" width="200px">
        <template slot-scope="{row}">
          <el-button
            size="mini"
            type="danger"
            icon="el-icon-close"
            @click="handleDelete(row)"
          >删除</el-button>
        </template>
      </el-table-column>

    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="100px" style="width: 400px; margin-left:100px;">
        <el-form-item label="奶牛场ID" prop="id">
          <el-input v-model="temp.id" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="temp.name" />
        </el-form-item>
        <el-form-item label="奶牛数量" prop="max_cow_no">
          <el-input v-model="temp.max_cow_no" :disabled="true"/>
        </el-form-item>
        <el-form-item label="奶桶数量" prop="max_bucket_no">
          <el-input v-model="temp.max_bucket_no" :disabled="true"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">
          确认
        </el-button>
      </div>
    </el-dialog>

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
import {fetchList, fetchPv, updateUser, deleteUser, createDairyFarm} from '@/api/dairyfarm'
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
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        importance: undefined,
        title: undefined,
        type: undefined,
      },
      importanceOptions: [1, 2, 3],
      calendarTypeOptions,
      statusOptions: ['published', 'draft', 'deleted'],
      showReviewer: false,
      temp: {
        id: '',
        name: '',
        create_time: '',
        max_cow_no: '',
        max_bucket_no: '',
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '更新奶牛场',
        create: '创建奶牛场'
      },
      dialogPvVisible: false,
      pvData: [],
      rules: {
        id: [{ required: true, message: '奶牛场ID不能为空', trigger: 'blur' }],
        name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
      },
      downloadLoading: false,
      timeDefault: new Date()
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getdatatime(){//默认显示今天
      this.create_time= new Date();
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
        id: '',
        name: '',
        create_time: '',
        max_cow_no: 0,
        max_bucket_no: 0,
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
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.temp.create_time = new Date()
          createDairyFarm(this.temp).then(() => {
            this.list.unshift(this.temp)
            this.dialogFormVisible = false
            this.$notify({
              title: 'Success',
              message: '奶牛场添加成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
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
