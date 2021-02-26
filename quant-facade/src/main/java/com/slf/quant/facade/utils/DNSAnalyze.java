package com.slf.quant.facade.utils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.xml.bind.DatatypeConverter;

/**
 * DNSUtil：域名解析工具，附带本地主机信息工具
 *
 * @author: fzk
 * @date: 2020-10-19 10:18
 */
public class DNSAnalyze
{
    private JFrame      frmDnsip;
    
    private JTextField  textField;
    
    private JScrollPane scrollPane;
    
    private JTextArea   textArea;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(() -> {
            try
            {
                DNSAnalyze window = new DNSAnalyze();
                window.frmDnsip.setVisible(true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }
    
    // 获取本机主机IP地址对象
    public void getLocalIP()
    {
        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            String hostAddr = addr.getHostAddress();// 获取IP地址
            String hostName = addr.getHostName();// 获取本机机器名
            System.out.println("本地IP地址：" + hostAddr);
            System.out.println("本地机器名：" + hostName);
            print("本地IP地址：" + hostAddr);
            print("本地机器名：" + hostName);
        }
        catch (UnknownHostException e)
        {// 捕获未知主机异常
            System.out.println("不能获得主机IP地址：" + e.getMessage());
            print("不能获得主机IP地址：" + e.getMessage());
            System.exit(1);
        }
    }
    
    // 根据域名获得主机的IP地址
    public void getIPByName(String hostName)
    {
        try
        {
            InetAddress addr = InetAddress.getByName(hostName);// 根据域名创建主机地址对象
            String hostAddr = addr.getHostAddress();// 获取主机IP地址
            System.out.println("域名为：" + hostName + "的主机IP地址：" + hostAddr);
            print("域名为：" + hostName + "的主机IP地址：" + hostAddr);
        }
        catch (UnknownHostException e)
        {
            System.out.println("不能根据域名获得主机IP地址：" + e.getMessage());
            print("不能根据域名获得主机IP地址：" + e.getMessage());
            System.exit(1);
        }
    }
    
    // 根据域名获得主机所有的IP地址
    public void getAllIPByName(String hostName)
    {
        try
        {
            InetAddress[] addrs = InetAddress.getAllByName(hostName);// 根据域名创建主机地址对象
            String[] ips = new String[addrs.length];
            System.out.println("域名为：" + hostName + "的所有的主机IP地址：");
            print("域名为：" + hostName + "的所有的主机IP地址：");
            for (int i = 0; i < addrs.length; i++)
            {
                ips[i] = addrs[i].getHostAddress();// 获取主机IP地址
                System.out.println(ips[i]);
                print(ips[i]);
            }
        }
        catch (UnknownHostException e)
        {
            System.out.println("不能根据域名获得 主机所有IP地址：" + e.getMessage());
            System.exit(1);
            print("不能根据域名获得 主机所有IP地址：" + e.getMessage());
        }
    }
    
    // 获取网络配置信息，返回字符串
    public String getNetworkInfo()
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            // 获取主机名
            String hostname = InetAddress.getLocalHost().getHostName();
            sb.append("本机主机名:" + hostname + "\n");
            sb.append("===================================================\n");
            // 获取网卡列表
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements())
            {
                NetworkInterface nic = interfaces.nextElement();
                // 获取每个网卡的信息
                sb.append("网络接口" + nic.getIndex() + "的配置信息----------------");
                sb.append("接口名称：" + nic.getName() + "\n");
                sb.append("显示名称：" + nic.getDisplayName() + "\n");
                // 接口状态
                sb.append("接口状态：");
                if (nic.isUp())
                {
                    sb.append("已经激活\n");
                }
                else
                {
                    sb.append("已经关闭！\n");
                }
                // 是否环回接口
                sb.append("是否环回接口:");
                if (nic.isLoopback())
                {
                    sb.append("环回接口\n");
                }
                else
                {
                    sb.append("非环回接口\n");
                }
                // 是否虚拟接口
                sb.append("是否虚拟接口:");
                if (nic.isLoopback())
                {
                    sb.append("虚拟接口\n");
                }
                else
                {
                    sb.append("物理接口\n");
                }
                // 接口MTU
                sb.append("接口MTU：" + nic.getMTU() + "\n");
                // 获取显示硬件地址
                byte[] macAddr = nic.getHardwareAddress();
                if (macAddr != null)
                {
                    sb.append("接口硬件地址（MAC地址）:" + DatatypeConverter.printHexBinary(macAddr) + "\n");
                }
                else
                {
                    sb.append("无法获取该网卡硬件地址\n");
                }
                // 获取IP地址列表
                sb.append("IP地址列表：");
                Enumeration<InetAddress> ipaddresses = nic.getInetAddresses();
                while (ipaddresses.hasMoreElements())
                {
                    sb.append("\t" + ipaddresses.nextElement().getHostAddress() + "\n");
                }
                sb.append("\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "程序运行异常，无法获取网络信息，请检查安全软件配置。";
        }
        return sb.toString();
    }
    // public void getYuming(){
    // }
    
    // 判断是否是一个IP
    static class test
    {
        public String 除去空格(String IP)
        {// 去掉IP字符串前后所有的空格
            while (IP.startsWith(" "))
            {
                IP = IP.substring(1, IP.length()).trim();
            }
            while (IP.endsWith(" "))
            {
                IP = IP.substring(0, IP.length() - 1).trim();
            }
            return IP;
        }
        
        public boolean isIp(String IP)
        {
            boolean b = false;
            IP = 除去空格(IP);
            if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"))
            {
                String s[] = IP.split("\\.");
                if (Integer.parseInt(s[0]) < 255)
                {
                    if (Integer.parseInt(s[1]) < 255)
                    {
                        if (Integer.parseInt(s[2]) < 255)
                        {
                            if (Integer.parseInt(s[3]) < 255)
                            {
                                b = true;
                            }
                        }
                    }
                }
            }
            return b;
        }
    }
    
    /**
     * 从源码内容中获取运营商
     * @param content
     * @return operator
     */
    /*
     * public String getOperatorFromContent(String hostName){
     * //找到以 '来' 开始 出现的位置
     * int beginIndex = hostName.indexOf("来自：");
     * //找到以 '</center>' 结束 出现的位置
     * int endIndex = hostName.indexOf("</center>");
     * //因为我们只需要截取运营商的名字，所以beginIndex需要加3
     * String operator = hostName.substring(beginIndex + 3, endIndex);
     * //返回运营商名称
     * return operator;
     * }
     */
    /**
     * Create the application.
     */
    public void print(String a)
    {
        textArea.append(a + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }
    
    public DNSAnalyze()
    {
        initialize();
    }
    
    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        frmDnsip = new JFrame();
        frmDnsip.setTitle("\u4F55\u540C\u5B66\u7684\u8BFE\u7A0B\u8BBE\u8BA1                                    DNS\u57DF\u540D\u89E3\u6790");
        frmDnsip.setBounds(100, 100, 670, 453);
        frmDnsip.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmDnsip.getContentPane().setLayout(null);
        // 输入文本框
        textField = new JTextField();
        textField.setBounds(10, 49, 631, 76);
        frmDnsip.getContentPane().add(textField);
        textField.setColumns(10);
        // 查询按钮
        JButton btnNewButton = new JButton("\u67E5\u8BE2");
        btnNewButton.setBounds(420, 150, 93, 38);
        frmDnsip.getContentPane().add(btnNewButton);
        btnNewButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // 当输入查询为空的时候弹出提示窗口(没成功)
                // String input=textField.getText();
                // if(textField.getText()==null){
                // JOptionPane.showMessageDialog(frmDnsip, "请输入需要解析的域名！");
                // return;
                // }
                // else{
                String s = textField.getText();
                // new test().isIp(s);
                if (new test().isIp(s) == true)
                {
                    // getLocalIP();//调用方法获得本机的IP地址
                    // String hostName=textField.getText();//获取xx域名
                    System.out.println("ip");
                    try
                    {
                        // 通过IP反查域名的方法
                        // String ip=textField.getText(); //IP地址
                        String[] ipStr = s.split("[.]"); // IP地址转换为字符串数组
                        byte[] ipBytes = new byte[4]; // 声明存储转换后IP地址的字节数组
                        for (int i = 0; i < 4; i++)
                        {
                            int m = Integer.parseInt(ipStr[i]); // 转换为整数
                            byte b = (byte) (m & 0xff); // 转换为字节
                            ipBytes[i] = b;
                        }
                        InetAddress inetAddr = InetAddress.getByAddress(ipBytes); // 创建InetAddress对象
                        String canonical = inetAddr.getCanonicalHostName(); // 获取域名
                        String host = inetAddr.getHostName(); // 获取主机名
                        textArea.setText(canonical); // 在文本框中显示域名
                        textArea.setText(host); // 在文本框中显示主机名
                    }
                    catch (Exception e2)
                    {
                        // TODO: handle exception
                        e2.printStackTrace();
                    }
                }
                else
                {
                    getLocalIP();// 调用方法获得本机的IP地址
                    String hostName = textField.getText();// 获取xx域名
                    getIPByName(hostName);// 获取xx域名的主机IP地址
                    System.out.println("一个域名对应多个ip地址，列举出" + hostName + "域名下的所有ip");
                    getAllIPByName(hostName);// 获取xx域名主机所有的IP地址
                    // System.out.println( "营运商名称："+getOperatorFromContent(hostName));;//获取域名的营运商
                }
                // }/
            }
        });
        // 输出提示标签
        JLabel lblNewLabel = new JLabel("\u8F93\u51FAIP\u5730\u5740\u548C\u672C\u5730\u673A\u5668\u540D\uFF1A");
        lblNewLabel.setBounds(10, 152, 155, 34);
        frmDnsip.getContentPane().add(lblNewLabel);
        scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 196, 631, 204);
        frmDnsip.getContentPane().add(scrollPane);
        // 输出文本框
        textArea = new JTextArea();
        scrollPane.setViewportView(textArea);
        // 输入提示标签
        JLabel lblNewLabel_1 = new JLabel("\u8BF7\u8F93\u5165\u9700\u8981\u67E5\u8BE2\u7684\u57DF\u540D\uFF1A");
        lblNewLabel_1.setBounds(10, 10, 155, 26);
        frmDnsip.getContentPane().add(lblNewLabel_1);
        // 清屏按钮
        JButton btnNewButton_1 = new JButton("\u6E05\u5C4F");
        btnNewButton_1.setBounds(551, 150, 93, 38);
        btnNewButton_1.addActionListener(arg0 -> {
            textArea.setText("");
            textField.setText("");
        });
        frmDnsip.getContentPane().add(btnNewButton_1);
        JButton btnNewButton_2 = new JButton("\u83B7\u53D6\u4E3B\u673A\u7F51\u7EDC\u63A5\u53E3\u4FE1\u606F");
        btnNewButton_2.setBounds(214, 148, 179, 40);
        btnNewButton_2.addActionListener(arg0 -> {
            textArea.setText("");
            String NicInfo = getNetworkInfo();
            textArea.append(NicInfo);
        });
        frmDnsip.getContentPane().add(btnNewButton_2);
    }
}
