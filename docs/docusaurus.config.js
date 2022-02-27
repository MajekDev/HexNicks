const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').DocusaurusConfig} */
module.exports = {
  title: 'HexNicks Docs',
  tagline: 'Documentation for HexNicks Minecraft Plugin',
  url: 'https://hexnicks.majek.dev/',
  baseUrl: '/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.png',
  projectName: 'hexnicks-docs',
  organizationName: 'Majekdor',
  deploymentBranch: 'docusaurus',
  trailingSlash: 'false',

  themeConfig: {
    colorMode: {
      defaultMode: 'dark',
      disableSwitch: false,
      respectPrefersColorScheme: true,
    },
    navbar: {
      title: 'HexNicks Docs',
      logo: {
        alt: 'HexNicks',
        src: 'img/favicon.png',
      },
      items: [],
    },
    prism: {
      theme: lightCodeTheme,
      darkTheme: darkCodeTheme,
      additionalLanguages: ['xml-doc', 'groovy', 'java'],
    },
  },

  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          routeBasePath: '/',
          sidebarPath: require.resolve('./sidebars.js'),
          sidebarCollapsible: false,
          sidebarCollapsed: false,
          editUrl: 'https://github.com/MajekDev/HexNicks/edit/main/docs/',
        },

        blog: false,
        pages: false
      },
    ],
  ],
};
