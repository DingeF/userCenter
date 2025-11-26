import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';
import {PLANT_DOC} from "@/constants";

const Footer: React.FC = () => {
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright="Powered by YiFan 2025"
      links={[
        {
          key: '知识星球文档',
          title: '知识星球',
          href: PLANT_DOC,
          blankTarget: true,  // 默认打开新页面跳转
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/ant-design/ant-design-pro',
          blankTarget: true,
        },
        {
          key: 'DingeF',
          title: 'DingeF',
          href: 'https://github.com/DingeF?tab=stars',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
