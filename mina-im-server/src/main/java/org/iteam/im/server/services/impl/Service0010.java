package org.iteam.im.server.services.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.iteam.im.core.IdsResp;
import org.iteam.im.core.Messages;
import org.iteam.im.core.mode.User;
import org.iteam.im.server.services.BaseService;
import org.iteam.im.utils.MongoUtils;
import org.iteam.mina.protocal.JMessageProtocalRequest;
import org.iteam.mina.protocal.JMessageProtocalResponse;
import org.iteam.mina.utils.DateUtils;
import org.iteam.mina.utils.GsonUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * 用户注册
 * 
 * @author wenke
 * 
 */
public class Service0010 implements BaseService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5653720942978598487L;
	private User user;

	@Override
	public boolean validation(JMessageProtocalRequest request,
			StringBuffer errorMsg) {
		if (StringUtils.isNotBlank(request.getContent())) {
			user = GsonUtils.fromJson(request.getContent(), User.class);
			if (StringUtils.isBlank(user.u_name)) {
				errorMsg.append("用户名称必填");
				return false;
			}
			if (StringUtils.isBlank(user.u_account)) {
				errorMsg.append("用户登录帐号必填");
				return false;
			} else {
				DBCollection dc = MongoUtils.getDB().getCollection(
						User.tableName);
				DBObject query = new BasicDBObject();
				query.put("u_account", user.u_account);
				DBObject res = dc.findOne(query);
				if (res != null) {
					errorMsg.append("该登录帐号已经存在，请勿重复注册");
					return false;
				}
			}
			if (StringUtils.isBlank(user.u_pwd)) {
				errorMsg.append("用户登录密码必填");
				return false;
			}
			errorMsg.append(Messages.VALIDATE_SUCCESS);
			return true;
		}
		errorMsg.append(Messages.VALIDATE_ERROR);
		return false;
	}

	@Override
	public JMessageProtocalResponse exec(JMessageProtocalRequest request,
			IoSession session) throws Exception {
		JMessageProtocalResponse response = new JMessageProtocalResponse();
		try {
			DBCollection dc = MongoUtils.getDB().getCollection(User.tableName);
			DBObject data = new BasicDBObject();
			data.put("u_account", user.u_account);

			synchronized (this) {
				DBObject res = dc.findOne(data);
				if (res != null) {
					// "用户信息已经存在"
					response.setResultCode(IdsResp.EXCEPTION);
					// response.setContent("用户信息已经存在");
					return response;
				}
				data.put("u_name", user.u_name);
				data.put("u_create_time", DateUtils.toDateString());
				data.put("u_pwd", user.u_pwd);
				data.put("u_status", 0);
				dc.insert(data);
				res = dc.findOne(data);
				response.setContent(res.get("_id").toString());
			}
			response.setResultCode(IdsResp.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			response.setResultCode(IdsResp.EXCEPTION);

		}
		return response;
	}

	public static void main(String[] args) {
		try {
			User user = new User();
			user.u_account = "admin1";
			user.u_name = "name";
			user.u_pwd = "u_pwd";
			DBCollection dc = MongoUtils.getDB().getCollection(User.tableName);
			DBObject data = new BasicDBObject();
			data.put("u_account", user.u_account);

			synchronized (data) {
				DBObject res = dc.findOne(data);
				if (res != null) {
					throw new Exception("用户信息已经存在");
				}
				data.put("u_name", user.u_name);
				data.put("u_create_time", DateUtils.toDateString());
				data.put("u_pwd", user.u_pwd);
				data.put("u_status", 0);
				dc.insert(data);
				res = dc.findOne(data);
				System.out.println(res.get("_id").toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
