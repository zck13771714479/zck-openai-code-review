package com.openai.code.review.infrastructure.git.dto;

/**
 * 接受
 */
public class SingleCommitResponseDTO {
    /**
     * 本次提交的哈希值
     */
    private String sha;
    private Commit commit;
    private CommitFile[] files;

    public static class Commit{
        /**
         * 提交的消息
         */
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class CommitFile{
        /**
         * 修改的文件名
         */
        private String filename;
        /**
         * 修改文件的url
         */
        private String raw_url;
        /**
         * diffCode
         */
        private String patch;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getRaw_url() {
            return raw_url;
        }

        public void setRaw_url(String raw_url) {
            this.raw_url = raw_url;
        }

        public String getPatch() {
            return patch;
        }

        public void setPatch(String patch) {
            this.patch = patch;
        }
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public CommitFile[] getFiles() {
        return files;
    }

    public void setFiles(CommitFile[] files) {
        this.files = files;
    }
}
