package com.openai.code.review.infrastructure.git;

import com.openai.code.review.utils.EnvUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * git命令交互
 */
public class GitCommand implements IBaseGitOperation {
    private final Logger logger = LoggerFactory.getLogger(GitCommand.class);

    private String reviewLogRepoURI = EnvUtils.getEnv("LOG_REPOSITORY_URL");
    /**
     * 项目名称
     */
    private String project;
    /**
     * 分支名称
     */
    private String branch;
    /**
     * 作者名称
     */
    private String author;
    /**
     * 提交时的消息
     */
    private String commitMessage;
    /**
     * github https的token 文档https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens
     */
    private String githubToken;

    /**
     * 写入日志的方式
     */
    private String writeType;

    /**
     * 提交的sha码
     */
    private String commitSHA;

    /**
     * 仓库拥有者
     */
    private String owner;

    public GitCommand(String project, String branch, String author, String commitMessage, String githubToken, String writeType, String commitSHA, String owner) {
        this.project = project;
        this.branch = branch;
        this.author = author;
        this.commitMessage = commitMessage;
        this.githubToken = githubToken;
        this.writeType = writeType;
        this.commitSHA = commitSHA;
        this.owner = owner;
    }


    /**
     * 获取2次提交的不同代码
     *
     * @return
     * @throws IOException
     */
    public String getDiffCode() throws IOException {
        //1. 统计分支提交次数
        ProcessBuilder countProcessBuilder = new ProcessBuilder("git", "rev-list", "--count", "HEAD");
        countProcessBuilder.directory(new File("."));
        Process countProcess = countProcessBuilder.start();

        BufferedReader countReader = new BufferedReader(new InputStreamReader(countProcess.getInputStream()));
        String countLine = countReader.readLine();
        countReader.close();
        int commitCount = Integer.parseInt(countLine.trim());
        if (commitCount < 2) {
            logger.info("branch has less than 2 commits");
            return "";
        }
        //2. 获取2次提交的不同 git diff
        ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", "HEAD~1", "HEAD");
        processBuilder.directory(new File("."));
        Process process = processBuilder.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder diffCode = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            diffCode.append(line);
        }
        return diffCode.toString();
    }

    /**
     * 写入评审日志到特定git仓库
     *
     * @param reviewResult
     * @return
     * @throws GitAPIException
     */
    @Override
    public String writeResult(String reviewResult) {
        try {
            Git git = Git.cloneRepository()
                    .setURI(this.reviewLogRepoURI + ".git")
                    .setDirectory(new File("repo"))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.githubToken, ""))
                    .call();
            String folderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            File folder = new File("repo/" + folderName);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String fileName = this.project + "-" + this.branch + "-" + this.author + "-" + this.generateRandomName(4) + ".md";
            File logFile = new File(folder, fileName);
            try (FileWriter writer = new FileWriter(logFile)) {
                writer.write(reviewResult);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            git.add().addFilepattern(folderName + "/" + fileName).call();
            git.commit().setMessage("Code Review Log").call();
            git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(this.githubToken, "")).call();
            return this.reviewLogRepoURI + "/blob/main/" + folderName + "/" + fileName;
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRandomName(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    public String getReviewLogRepoURI() {
        return reviewLogRepoURI;
    }

    public void setReviewLogRepoURI(String reviewLogRepoURI) {
        this.reviewLogRepoURI = reviewLogRepoURI;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public String getGithubToken() {
        return githubToken;
    }

    public void setGithubToken(String githubToken) {
        this.githubToken = githubToken;
    }

    public String getWriteType() {
        return writeType;
    }

    public void setWriteType(String writeType) {
        this.writeType = writeType;
    }

    public String getCommitSHA() {
        return commitSHA;
    }

    public void setCommitSHA(String commitSHA) {
        this.commitSHA = commitSHA;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
