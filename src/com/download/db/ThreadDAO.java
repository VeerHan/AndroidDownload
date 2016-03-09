package com.download.db;

import java.util.List;

import com.download.entities.ThreadInfo;

/**
 * 数据访问接口
 * 
 * @author Leo
 */
public interface ThreadDAO {
	/**
	 * 插入线程信息
	 * 
	 * @param threadInfo
	 * @return void
	 * @author Leo
	 */
	public void insertThread(ThreadInfo threadInfo);

	/**
	 * 删除线程信息
	 * 
	 * @param url
	 * @param thread_id
	 * @return void
	 * @author Leo
	 */
	public void deleteThread(String url, int thread_id);

	/**
	 * 更新线程下载进度
	 * 
	 * @param url
	 * @param thread_id
	 * @return void
	 * @author Leo
	 */
	public void updateThread(String url, int thread_id, int finished);

	/**
	 * 查询文件的线程信息
	 * 
	 * @param url
	 * @return
	 * @return List<ThreadInfo>
	 * @author Leo
	 */
	public List<ThreadInfo> getThreads(String url);

	/**
	 * 线程信息是否存在
	 * 
	 * @param url
	 * @param thread_id
	 * @return
	 * @return boolean
	 * @author Leo
	 */
	public boolean isExists(String url, int thread_id);
}
