package com.wuxing.sortlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wuxing.activity.BaseActivity;
import com.wuxing.activity.LuXianActivity;
import com.wuxing.activity.R;
import com.wuxing.bean.Member;
import com.wuxing.bean.UserBean;
import com.wuxing.bean.UserBean.Data;
import com.wuxing.sortlist.SideBar.OnTouchingLetterChangedListener;
import com.wuxing.utils.Constant;
import com.wuxing.utils.NetUtils;
import com.wuxing.utils.ToastUtil;
import com.wuxing.villoy.VolleyInterface;
import com.wuxing.villoy.VolleyRequest;

/**
 * 
 * @author Administrator
 *
 */
public class SortListViewMainActivity extends BaseActivity implements
		OnClickListener {
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	private ImageView iv_back;
	private Button btn_sure;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private List<SortModel> mList;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	private String groupName, groupId;
	private String flag;
	private List<Member> members;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview_sort_main);
		initView();
		groupName = getIntent().getStringExtra("groupName");
		groupId = getIntent().getStringExtra("groupid");
		if (groupName == null) {
			flag = getIntent().getStringExtra("flag");
			members = (List<Member>) getIntent().getSerializableExtra("mList");
			if (flag.equals("del")) {
				// ToastUtil.show(SortListViewMainActivity.this, "删除界面");
				// 删除好友界面显示该组的成员列表
				mList = new ArrayList<SortModel>();
				for (int i = 0; i < members.size(); i++) {
					SortModel sort = new SortModel();
					String alias = members.get(i).getAlias();
					String user_name = members.get(i).getUser_name();
					String headimg = members.get(i).getHeadimg();

					if (alias != null) {
						sort.setAlias(alias);
					} else {
						sort.setAlias("");
					}
					sort.setName(user_name);
					sort.setImgPath(headimg);
					sort.setCheck(false);
					mList.add(sort);
				}
				SourceDateList.clear();
				SourceDateList.addAll(filledData(mList));
				// 根据a-z进行排序源数据
				Collections.sort(SourceDateList, pinyinComparator);
				adapter.notifyDataSetChanged();
			} else {
				// ToastUtil.show(SortListViewMainActivity.this, "添加界面");
				startProgressDialog();
				Map<String, String> map = new HashMap<String, String>();
				map.put("url", "/wx_ajax_c/getFriends/");
				map.put("username", sp.getString("hxusername", ""));
				String newUrl = Constant.SERVER_ADDRESS
						+ NetUtils.getUrlParamsByMap(map);
				VolleyRequest.requestGet(this, newUrl, "getFriends",
						new VolleyInterface(this, VolleyInterface.mListener,
								VolleyInterface.mErrorListener) {

							@Override
							public void onMySuccess(String result) {
								// TODO Auto-generated method stub
								Log.e("好友列表", result);
								Gson gson = new Gson();
								UserBean userBean = gson.fromJson(result,
										UserBean.class);
								if (userBean.getStatus().getSucceed() == 1) {
									stopProgressDialog();
									List<Data> strs = new ArrayList<UserBean.Data>();
									List<Data> datas = userBean.getData();
									for (int i = 0; i < members.size(); i++) {
										Member member = members.get(i);
										String user_name = member
												.getUser_name();

										for (int j = 0; j < datas.size(); j++) {
											if (user_name.equals(datas.get(j)
													.getUser_name())) {
												strs.add(datas.get(j));
											}
										}

									}

									// 删除已经是好友的好友
									if (strs.size() > 0) {

										datas.removeAll(strs);

									}

									mList = new ArrayList<SortModel>();
									for (int i = 0; i < datas.size(); i++) {
										SortModel sort = new SortModel();
										String alias = datas.get(i).getAlias();
										String user_name = datas.get(i)
												.getUser_name();
										String headimg = datas.get(i)
												.getHeadimg();

										if (alias != null) {
											sort.setAlias(alias);
										} else {
											sort.setAlias("");
										}
										sort.setName(user_name);
										sort.setImgPath(headimg);
										sort.setCheck(false);
										mList.add(sort);
									}
									SourceDateList.clear();
									SourceDateList.addAll(filledData(mList));
									// 根据a-z进行排序源数据
									Collections.sort(SourceDateList,
											pinyinComparator);
									adapter.notifyDataSetChanged();
								} else {
									stopProgressDialog();
									ToastUtil.show(
											SortListViewMainActivity.this,
											"error:"
													+ "succeed:"
													+ userBean.getStatus()
															.getSucceed() + "");
								}
							}

							@Override
							public void onMyError(VolleyError error) {
								// TODO Auto-generated method stub
								ToastUtil.show(SortListViewMainActivity.this,
										"获取列表错误" + error.getMessage());
							}
						});

			}

		} else {
			ToastUtil.show(SortListViewMainActivity.this, "好友列表界面");
			// url=/wx_ajax_c/getFriends/&username=u132GPGT9928
			startProgressDialog();
			Map<String, String> map = new HashMap<String, String>();
			map.put("url", "/wx_ajax_c/getFriends/");
			map.put("username", sp.getString("hxusername", ""));
			String newUrl = Constant.SERVER_ADDRESS
					+ NetUtils.getUrlParamsByMap(map);
			VolleyRequest.requestGet(this, newUrl, "getFriends",
					new VolleyInterface(this, VolleyInterface.mListener,
							VolleyInterface.mErrorListener) {

						@Override
						public void onMySuccess(String result) {
							// TODO Auto-generated method stub
							Log.e("好友列表", result);
							Gson gson = new Gson();
							UserBean userBean = gson.fromJson(result,
									UserBean.class);
							if (userBean.getStatus().getSucceed() == 1) {
								stopProgressDialog();
								List<Data> datas = userBean.getData();
								mList = new ArrayList<SortModel>();
								for (int i = 0; i < datas.size(); i++) {
									SortModel sort = new SortModel();
									String alias = datas.get(i).getAlias();
									String user_name = datas.get(i)
											.getUser_name();
									String headimg = datas.get(i).getHeadimg();

									if (alias != null) {
										sort.setAlias(alias);
									} else {
										sort.setAlias("");
									}
									sort.setName(user_name);
									sort.setImgPath(headimg);
									sort.setCheck(false);
									mList.add(sort);
								}
								SourceDateList.clear();
								SourceDateList.addAll(filledData(mList));
								// 根据a-z进行排序源数据
								Collections.sort(SourceDateList,
										pinyinComparator);
								adapter.notifyDataSetChanged();
							} else {
								stopProgressDialog();
								ToastUtil.show(SortListViewMainActivity.this,
										"error:"
												+ "succeed:"
												+ userBean.getStatus()
														.getSucceed() + "");
							}
						}

						@Override
						public void onMyError(VolleyError error) {
							// TODO Auto-generated method stub
							ToastUtil.show(SortListViewMainActivity.this,
									"获取列表错误" + error.getMessage());
						}
					});
		}

	}

	public void initView() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) findViewById(R.id.sidrbar);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		btn_sure = (Button) findViewById(R.id.btn_sure);
		btn_sure.setEnabled(false);
		btn_sure.setTextColor(Color.GRAY);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		iv_back.setOnClickListener(this);
		btn_sure.setOnClickListener(this);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				SortModel sort = (SortModel) adapter.getItem(position);
				if (sort.isCheck()) {

					sort.setCheck(false);

				} else {
					sort.setCheck(true);
				}

				adapter.notifyDataSetChanged();

				for (int i = 0; i < SourceDateList.size(); i++) {
					SortModel sm = SourceDateList.get(i);
					if (sm.isCheck()) {
						if (!btn_sure.isEnabled()) {
							btn_sure.setEnabled(true);
							btn_sure.setTextColor(Color.WHITE);
						}
						return;
					} else {
						if (btn_sure.isEnabled()) {
							btn_sure.setEnabled(false);
							btn_sure.setTextColor(Color.GRAY);
						}
					}
				}

			}
		});

		SourceDateList = new ArrayList<SortModel>();
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.iv_back:

			finish();
			break;
		case R.id.btn_sure:
			String groupusers;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < SourceDateList.size(); i++) {
				SortModel sort = SourceDateList.get(i);
				if (sort.isCheck()) {
					sb.append(SourceDateList.get(i).getName()).append(",");
				}
			}
			groupusers = sb.toString().substring(0,
					sb.toString().lastIndexOf(","));
			Log.e("groupusers", groupusers);
			if (groupName != null) {
				// ?url=/wx_ajax_c/add_group
				// &groupusers=zhangsan2
				// &username=u132GPGT9928
				// &groupname=车队
				Map<String, String> map = new HashMap<String, String>();
				map.put("url", "/wx_ajax_c/add_group");
				map.put("username", sp.getString("hxusername", ""));
				map.put("groupname", groupName);
				map.put("groupusers", groupusers);
				String newUrl = Constant.SERVER_ADDRESS
						+ NetUtils.getUrlParamsByMap(map);
				VolleyRequest.requestGet(SortListViewMainActivity.this, newUrl,
						"add_group", new VolleyInterface(
								SortListViewMainActivity.this,
								VolleyInterface.mListener,
								VolleyInterface.mErrorListener) {

							@Override
							public void onMySuccess(String result) {
								// TODO Auto-generated method stub
								// {"success":"True","groupid":"204227252274069944"}
								try {
									JSONObject job = new JSONObject(result);
									boolean success = job.getBoolean("success");
									if (success) {
										ToastUtil.show(
												SortListViewMainActivity.this,
												"创建成功！");
									} else {
										ToastUtil.show(
												SortListViewMainActivity.this,
												job.getString("message"));
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

							@Override
							public void onMyError(VolleyError error) {
								// TODO Auto-generated method stub

							}
						});

				setResult(RESULT_OK);
				finish();
			} else {
				if (flag.equals("del")) {
					// 访问删除好友
					ToastUtil.show(SortListViewMainActivity.this, "del");
					// http://www.5xclw.cn/ecmobile/
					// ?url=/wx_ajax_c/delete_group_users
					// &groupusers=zhangsan
					// &groupid=204932658688426428

					Map<String, String> map = new HashMap<String, String>();
					map.put("url", "/wx_ajax_c/delete_group_users");
					map.put("groupusers", groupusers);
					Log.e("删除时的groupusers", groupusers);
					map.put("groupid", groupId);
					String url = Constant.SERVER_ADDRESS
							+ NetUtils.getUrlParamsByMap(map);
					VolleyRequest.requestGet(SortListViewMainActivity.this,
							url, "delusers", new VolleyInterface(
									SortListViewMainActivity.this,
									VolleyInterface.mListener,
									VolleyInterface.mErrorListener) {

								@Override
								public void onMySuccess(String result) {
									// TODO Auto-generated method stub

									Log.e("删除组成员", result);
								}

								@Override
								public void onMyError(VolleyError error) {
									// TODO Auto-generated method stub

									ToastUtil.show(
											SortListViewMainActivity.this,
											"删除失败");
								}
							});
					finish();

				} else {
					// 访问添加好友
					ToastUtil.show(SortListViewMainActivity.this, "add");
					// http://www.5xclw.cn/ecmobile/
					// ?url=/wx_ajax_c/add_group_users
					// &groupusers=zhangsan
					// &groupid=204932658688426428

					Map<String, String> map = new HashMap<String, String>();
					map.put("url", "/wx_ajax_c/add_group_users");
					map.put("groupusers", groupusers);
					Log.e("添加时的groupusers", groupusers);
					map.put("groupid", groupId);
					String url = Constant.SERVER_ADDRESS
							+ NetUtils.getUrlParamsByMap(map);
					VolleyRequest.requestGet(SortListViewMainActivity.this,
							url, "getusers", new VolleyInterface(
									SortListViewMainActivity.this,
									VolleyInterface.mListener,
									VolleyInterface.mErrorListener) {

								@Override
								public void onMySuccess(String result) {
									// TODO Auto-generated method stub

									Log.e("添加组成员", result);
								}

								@Override
								public void onMyError(VolleyError error) {
									// TODO Auto-generated method stub

									ToastUtil.show(
											SortListViewMainActivity.this,
											"添加失败");
								}
							});
					finish();
				}

			}
			break;
		default:

			break;
		}

	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(List<SortModel> mList) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < mList.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(mList.get(i).getName());
			sortModel.setCheck(mList.get(i).isCheck());
			sortModel.setImgPath(mList.get(i).getImgPath());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(mList.get(i).getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

}
