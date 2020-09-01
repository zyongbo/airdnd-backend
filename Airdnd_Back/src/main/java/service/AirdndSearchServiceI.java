package service;

import java.util.List;

import vo.AirdndHomePictureVO;
import vo.AirdndSearchVO;
import vo.AirdndUserVO;

public interface AirdndSearchServiceI {
	
	List<AirdndSearchVO> searchselect(String place, int page);
	
	List<AirdndHomePictureVO> pictureselect(int home_idx);
	
	List<AirdndSearchVO> searchtotalselect(String place);

	List<AirdndUserVO> hostLanlist(String place);
	
}
