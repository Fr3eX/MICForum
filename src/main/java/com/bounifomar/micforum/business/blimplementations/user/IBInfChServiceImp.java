package com.bounifomar.micforum.business.blimplementations.user;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.bounifomar.micforum.business.blexceptions.FormVException;
import com.bounifomar.micforum.business.blexceptions.UnexpectedBehaviorException;
import com.bounifomar.micforum.business.blinterfaces.user.IBInfChService;
import com.bounifomar.micforum.business.blinterfaces.user.IBStorageService;
import com.bounifomar.micforum.models.muser.User;
import com.bounifomar.micforum.models.muser.UserGender;
import com.bounifomar.micforum.repositories.UserDAO;

import static com.bounifomar.micforum.business.blimplementations.utility.RequestParsUtility.*;


@Service
public class IBInfChServiceImp implements IBInfChService {
	
	private static final String SESSION_USER_ATTR = "USER_SESS";

	private static final String ERROR_ATTRIBUTE = "ERRORS";
	private static final String RESULT_ATTRIBUTE = "RESULT";
	
	private static final Integer MAX_BIGT_LENGTH = 255;
	private static final Integer MAX_MEDT_LENGTH = 30;


	private static final String USERCOVERPIC_PFIELD = "user_coverpic";
	private static final String USERPROFPIC_PFIELD = "user_profpic";

	private static final String USERADDRESS_PFIELD = "user_address";
	private static final String USERCOUNTRY_PFIELD = "user_country";
	private static final String USERCITY_PFIELD = "user_city";
	private static final String USERBIRTHDATE_PFIELD = "user_birthdate";
	private static final String USERGENDER_PFIELD = "user_gender";
	private static final String USERSIGNATURE_PFIELD = "user_signature";

	
	private static final String USERFBURL_PFIELD = "user_facebook";
	private static final String USERFGITURL_PFIELD = "user_github";
	private static final String USERTWITTERURL_PFIELD = "user_twitter";

	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	
	
	@Autowired
	private UserDAO userRep;
	
	@Autowired
	private IBStorageService storageService;
	
	
	@Override
	public void modUser(HttpServletRequest request,Model model)throws UnexpectedBehaviorException{
		
			
				Map<String,String> errors = new HashMap<String,String>();
				
				model.addAttribute(ERROR_ATTRIBUTE, errors);

				
				String user_address = getFieldValue(request, USERADDRESS_PFIELD);
				String user_country = getFieldValue(request, USERCOUNTRY_PFIELD);
				String user_city = getFieldValue(request, USERCITY_PFIELD);
				String user_birthdate = getFieldValue(request, USERBIRTHDATE_PFIELD);
				String user_gender = getFieldValue(request, USERGENDER_PFIELD);
				String user_signature = getFieldValue(request, USERSIGNATURE_PFIELD);
				
				String user_fburl = getFieldValue(request, USERFBURL_PFIELD);
				String user_twitterurl = getFieldValue(request, USERTWITTERURL_PFIELD);
				String user_giturl = getFieldValue(request, USERFGITURL_PFIELD);

				
				
				User user = (User)request.getSession().getAttribute(SESSION_USER_ATTR);
				User user_tmp = new User();
				
				
				if(user == null)
					throw new UnexpectedBehaviorException("[MOD USER SERVICE] User == null");
				
				
				user_tmp.setUser_address(user.getUser_address());
				user_tmp.setUser_country(user.getUser_country());
				user_tmp.setUser_city(user.getUser_city());
				user_tmp.setUser_birthdate(user.getUser_birthdate());
				user_tmp.setUser_signature(user.getUser_signature());
				user_tmp.setUser_fburl(user.getUser_fburl());
				user_tmp.setUser_twitterurl(user.getUser_twitterurl());
				user_tmp.setUser_githuburl(user.getUser_githuburl());
				user_tmp.setUser_coverpic_path(user.getUser_coverpic_path());
				user_tmp.setUser_pic_path(user.getUser_pic_path());
				user_tmp.setUser_age(user.getUser_age());
				
				
				
				String coverpic_prefname  = getPartStream_name(request, USERCOVERPIC_PFIELD, errors);
				String profpic_prefname = getPartStream_name(request, USERPROFPIC_PFIELD, errors);
			
				
				
				/*
				 * It's useless to keep checking the other fields if an error occurs while parsing file part fields
				 */
				if(!errors.isEmpty())
					return;
			
				
				
				this.genericProcessFields(user, USERADDRESS_PFIELD, user_address, MAX_BIGT_LENGTH, errors);
				this.genericProcessFields(user, USERCOUNTRY_PFIELD, user_country, MAX_MEDT_LENGTH, errors);
				this.genericProcessFields(user, USERCITY_PFIELD, user_city, MAX_MEDT_LENGTH, errors);
				this.genericProcessFields(user, USERSIGNATURE_PFIELD, user_signature, MAX_BIGT_LENGTH, errors);
				this.genericProcessFields(user, USERFBURL_PFIELD, user_fburl, MAX_BIGT_LENGTH, errors);
				this.genericProcessFields(user, USERFGITURL_PFIELD, user_giturl, MAX_BIGT_LENGTH, errors);
				this.genericProcessFields(user, USERTWITTERURL_PFIELD, user_twitterurl, MAX_BIGT_LENGTH, errors);
				
				this.processDate(user_birthdate, user, errors);
				this.processGender(user_gender, user);
				
				LocalDateTime datetime = LocalDateTime.now();
				
							
				try {
							
						if(errors.isEmpty())
						{	
							if(coverpic_prefname != null)
								user.setUser_coverpic_path(storageService.storeImage(request.getPart(USERCOVERPIC_PFIELD), 
									user.getUser_id()+datetime.getDayOfMonth()+datetime.getYear()+( coverpic_prefname.hashCode()& 0xfffffff)+""));
								
							if(profpic_prefname != null)
								user.setUser_pic_path(storageService.storeImage(request.getPart(USERPROFPIC_PFIELD),
										user.getUser_id()+datetime.getDayOfMonth()+datetime.getYear()+( profpic_prefname.hashCode()& 0xfffffff)+""));
							
							userRep.save(user);	
		
							model.addAttribute(RESULT_ATTRIBUTE, "Vos donn??es ont ??t?? changer avec succ??s.");
						}else
						{
							user.setUser_address(user_tmp.getUser_address());
							user.setUser_country(user_tmp.getUser_country());
							user.setUser_city(user_tmp.getUser_city());
							user.setUser_birthdate(user_tmp.getUser_birthdate());
							user.setUser_signature(user_tmp.getUser_signature());
							user.setUser_fburl(user_tmp.getUser_fburl());
							user.setUser_twitterurl(user_tmp.getUser_twitterurl());
							user.setUser_githuburl(user_tmp.getUser_githuburl());
							user.setUser_coverpic_path(user_tmp.getUser_coverpic_path());
							user.setUser_pic_path(user_tmp.getUser_pic_path());
							user.setUser_age(user_tmp.getUser_age());

						}
			
					}
					catch (Exception e) {
						//Generate a message when unexpected error occurs
						errors.put(USERCOVERPIC_PFIELD, "Une erreur inattendue est survenu r??essayer ult??rieurement");
						user.setUser_address(user_tmp.getUser_address());
						user.setUser_country(user_tmp.getUser_country());
						user.setUser_city(user_tmp.getUser_city());
						user.setUser_birthdate(user_tmp.getUser_birthdate());
						user.setUser_signature(user_tmp.getUser_signature());
						user.setUser_fburl(user_tmp.getUser_fburl());
						user.setUser_twitterurl(user_tmp.getUser_twitterurl());
						user.setUser_githuburl(user_tmp.getUser_githuburl());
						user.setUser_coverpic_path(user_tmp.getUser_coverpic_path());
						user.setUser_pic_path(user_tmp.getUser_pic_path());
						user.setUser_age(user_tmp.getUser_age());

						e.printStackTrace();
					}
			
	}
	
	
	private void genericProcessFields(User user,String field_name,String field_value,Integer comparedTo,Map<String,String> errors)
	{
		if(field_value == null)
			return;
		
		try
		{
			this.genericComparisonOver(field_name, field_value, comparedTo);
			
			switch(field_name)
			{	
				case USERADDRESS_PFIELD:
					if(user.getUser_address() == null)
					{
						user.setUser_address(field_value);
						break;
					}
					if(!user.getUser_address().equals(field_value))
						user.setUser_address(field_value);
					break;
				case USERCOUNTRY_PFIELD:
					if(user.getUser_country() == null)
					{
						user.setUser_country(field_value);
						break;
					}
					if(!user.getUser_country().equals(field_value))
						user.setUser_country(field_value);
					break;
				case USERCITY_PFIELD:
					if(user.getUser_city() == null)
					{
						user.setUser_city(field_value);
						break;
					}
					if(!user.getUser_city().equals(field_value))
						user.setUser_city(field_value);
					break;
				case USERSIGNATURE_PFIELD:
					if(user.getUser_signature() == null)
					{
						user.setUser_signature(field_value);
						break;
					}
					if(!user.getUser_signature().equals(field_value))
						user.setUser_signature(field_value);
					break;
				case USERFBURL_PFIELD:
					if(user.getUser_fburl() == null)
					{
						user.setUser_fburl(field_value);
						break;
					}
					if(!user.getUser_fburl().equals(field_value))
						user.setUser_fburl(field_value);
					break;
				case USERFGITURL_PFIELD:
					if(user.getUser_githuburl() == null)
					{
						user.setUser_githuburl(field_value);
						break;
					}
					if(!user.getUser_githuburl().equals(field_value))
						user.setUser_githuburl(field_value);
					break;
				case USERTWITTERURL_PFIELD:
					if(user.getUser_twitterurl() == null)
					{
						user.setUser_twitterurl(field_value);
						break;
					}
					if(!user.getUser_twitterurl().equals(field_value))
						user.setUser_twitterurl(field_value);
					break;
			}	
		}
		catch (FormVException e) {
			errors.put(field_name, e.getMessage());
		}
	}
	
	private void processDate(String date,User user,Map<String,String> errors)
	{
		if(date == null)
			return;
		
		try
		{
			LocalDateTime datetime = LocalDateTime.now();
			Calendar actualBdate = Calendar.getInstance(),newBD=Calendar.getInstance();
			
			newBD.setTime(new SimpleDateFormat(DATE_FORMAT).parse(date));
			
			if(user.getUser_birthdate() == null)
			{
				user.setUser_birthdate(newBD.getTime());
				return;
			}
			
			actualBdate.setTime(user.getUser_birthdate());

			if((actualBdate.get(Calendar.DAY_OF_MONTH) == newBD.get(Calendar.DAY_OF_MONTH))  && 
				(actualBdate.get(Calendar.YEAR) == newBD.get(Calendar.YEAR)) &&
				((actualBdate.get(Calendar.MONTH) + 1) == (newBD.get(Calendar.MONTH) + 1)))	
					return;
			else
			{
				 Integer sub = datetime.getYear() - newBD.get(Calendar.YEAR);
				 
				 if(sub < 0)
					 throw new FormVException("Date invalide .");
				
				user.setUser_birthdate(newBD.getTime());
				user.setUser_age(sub);
				
			}
		}
		catch (ParseException e) {
			errors.put(USERBIRTHDATE_PFIELD,"Format de la date est incorrecte.");
		}
		catch (FormVException e) {
			errors.put(USERBIRTHDATE_PFIELD,e.getMessage());
		}
	}
	
	
	
	
	private void processGender(String gender,User user)
	{
		if(gender == null)
			return;
	
		switch(gender)
		{
			case "f":
				if(user.getUser_gender() == UserGender.MALE)
					return;
				
				user.setUser_gender(UserGender.MALE);
				
				break;
			case "m":
				
				if(user.getUser_gender() == UserGender.FEMALE)
					return;
				user.setUser_gender(UserGender.FEMALE);
				
				break;
			default:
				
				if(user.getUser_gender() == UserGender.OTHER)
					return;
				user.setUser_gender(UserGender.OTHER);
				
				break;
		}
		
	}
	
	
	
	
	private void genericComparisonOver(String field_name,String field_value,Integer comparedTo)throws FormVException
	{
		if(field_value.length() > comparedTo)
		{	
			switch(field_name)
			{	
				case USERADDRESS_PFIELD:
					throw new FormVException("L'adresse ne peut pas d??passer "+comparedTo+" caract??res.");
				case USERCOUNTRY_PFIELD:
					throw new FormVException("Pays ne peut pas d??passer "+comparedTo+" caract??res.");
				case USERCITY_PFIELD:
					throw new FormVException("Ville ne peut pas d??passer "+comparedTo+" caract??res.");
				case USERSIGNATURE_PFIELD:
					throw new FormVException("La signature ne peut pas d??passer "+comparedTo+" caract??res.");
				case USERFBURL_PFIELD:
					throw new FormVException("Facebook url ne peut pas d??passer "+comparedTo+" caract??res.");
				case USERFGITURL_PFIELD:
					throw new FormVException("Github url ne peut pas d??passer "+comparedTo+" caract??res.");
				case USERTWITTERURL_PFIELD:
					throw new FormVException("Twitter url ne peut pas d??passer "+comparedTo+" caract??res.");
			}	
		}
	}
	

	

}
