package com.xxxxxxH.netinfo.sendmain;

public class EmailUtil {
    private static final String TAG = "EmailUtil";

    /**
     * @param title       邮件的标题
     * @param content     邮件的文本内容
     * @param toAddress   收件人的地址  如：xxx@qq.com
     * @param host        发送方smtp地址
     * @param fromAddress 发送方邮箱地址
     * @param fromPwd     发送方邮箱授权码
     * @param filePath    附件
     */
    public static boolean autoSendMail(String title, String content, String toAddress, String host, String fromAddress, String fromPwd, String[] filePath) {
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost(host);
        mailInfo.setValidate(true);
        mailInfo.setUserName(fromAddress);
        mailInfo.setPassword(fromPwd);
        mailInfo.setFromAddress(fromAddress);
        mailInfo.setToAddress(toAddress);
        mailInfo.setSubject(title);
        mailInfo.setContent(content);
        // 这个类主要来发送邮件
        SimpleMailSender sms = new SimpleMailSender();
        if (filePath == null || filePath.length == 0)
            return sms.sendTextMail(mailInfo);
        else
            return sms.sendTextAndFileMail(mailInfo, filePath);
    }

}
