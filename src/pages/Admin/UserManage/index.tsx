import React, {useState} from 'react';
import {PlusOutlined} from '@ant-design/icons';
import type {ActionType, ProColumns} from '@ant-design/pro-components';
import {ProTable, TableDropdown} from '@ant-design/pro-components';
import {Button, Dropdown, Space, Tag, Image} from 'antd';
import {useRef} from 'react';
import {queryUserList} from "@/services/ant-design-pro/api";

export const waitTimePromise = async (time: number = 100) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(true);
    }, time);
  });
};

export const waitTime = async (time: number = 100) => {
  await waitTimePromise(time);
};

const columns: ProColumns<API.CurrentUser>[] = [
  {
    dataIndex: 'id',  // dataIndex:将表格列名与数据库字段名匹配上即可
    valueType: 'indexBorder',
    width: 48,
  },
  {
    title: '用户账户',
    dataIndex: 'userAccount',
    copyable: true,
  },
  {
    title: '用户名',
    dataIndex: 'username',
    filters: true,
    onFilter: true,
  },
  {
    title: '用户头像',
    dataIndex: 'avatarUrl',
    search: false,
    render: (_, record) => (
      <Image
        src={record.avatarUrl}
        width={56}
        height={56}
        style={{ borderRadius: '50%', objectFit: 'cover' }}
        preview={{ src: record.avatarUrl }}
      />
    ),
  },
  {
    title: '性别',
    key: 'showTime',
    dataIndex: 'gender',
    search: true,
    valueType:"select",
    fieldProps: {
      options: [
        {label:"男",value:"1"},
        {label: "女",value:"0"},
      ]
    },
   },
  {
    title: '电话',
    dataIndex: 'phone',
  },
  {
    title: '邮箱',
    dataIndex: 'email',
  },
  {
    title: '用户状态',
    dataIndex: 'userStatus',
    valueType: 'select',
    fieldProps:{
      options: [
        {label:'正常',value:"0"},
        {label: '非正常',value:"1"},
      ]
    }
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    search: true,
    valueType:"select",
    fieldProps: {
      options: [
        {label:"普通用户",value:"0"},
        {
          label: (
            <span>
              <span
                style={{
                  display: 'inline-block',
                  width: 8,
                  height: 8,
                  borderRadius: '50%',
                  backgroundColor: '#52c41a',
                  marginRight: 6,
                }}
              />
              管理员
            </span>
          ),
          value:"1"
        },
      ]
    },
  },
  {
    title: '用户编码',
    dataIndex: 'plantCode',
  },
  {
    title: '创建时间',
    key: 'showTime',
    dataIndex: 'createTime',
    valueType: 'date',
    sorter: true,
    hideInSearch: true,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    valueType: 'dateRange',
    hideInTable: true,
    search: {
      transform: (value) => {
        return {
          startTime: value[0],
          endTime: value[1],
        };
      },
    },
  },
  {
    title: '操作',
    valueType: 'option',
    key: 'option',
    render: (text, record, _, action) => [
      <a
        key="editable"
        onClick={() => {
          action?.startEditable?.(record.id);
        }}
      >
        编辑
      </a>,
      <a href={record.url} target="_blank" rel="noopener noreferrer" key="view">
        查看
      </a>,
      <TableDropdown
        key="actionGroup"
        onSelect={() => action?.reload()}
        menus={[
          {key: 'copy', name: '复制'},
          {key: 'delete', name: '删除'},
        ]}
      />,
    ],
  },
];

export default () => {
  const actionRef = useRef<ActionType>(null);
  return (
    <ProTable<API.CurrentUser>
      columns={columns}
      actionRef={actionRef}
      cardBordered
      request={async (params, sort, filter) => {
        await waitTime(200);
        const res = await queryUserList();
        return {
          data: res.data,
          success: true,
        };
      }}
      editable={{
        type: 'multiple',
      }}
      columnsState={{
        persistenceKey: 'pro-table-singe-demos',
        persistenceType: 'localStorage',
        defaultValue: {
          option: {fixed: 'right', disable: true},
        },
        onChange(value) {
          console.log('value: ', value);
        },
      }}
      rowKey="id"
      search={{
        labelWidth: 'auto',
      }}
      options={{
        setting: {
          listsHeight: 400,
        },
      }}
      form={{
        // 由于配置了 transform，提交的参数与定义的不同这里需要转化一下
        syncToUrl: (values, type) => {
          if (type === 'get') {
            return {
              ...values,
              created_at: [values.startTime, values.endTime],
            };
          }
          return values;
        },
      }}
      pagination={{
        pageSize: 5,
        onChange: (page) => console.log(page),
      }}
      dateFormatter="string"
      headerTitle="高级表格"
      toolBarRender={() => [
        <Button
          key="button"
          icon={<PlusOutlined/>}
          onClick={() => {
            actionRef.current?.reload();
          }}
          type="primary"
        >
          新建
        </Button>,

      ]}
    />
  );
};
