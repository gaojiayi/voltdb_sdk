#!/bin/bash
v_voltdb="voltdb"
v_jdk_install_path="$HOME/"

#
main()
{

	echo -n "Do you install JDK1.6? [y/n default y] :"
	v_temp=`check`
	if [[ ${v_temp}X = X || ${v_temp} = 'Y' ]]; then
		#statements
		installJdk

	fi

	echo "To install voltdb."
	if [[ -d $v_voltdb ]]; then
		#statements
		echo -n "Are you sure you want to overwrite the VOLTDB directory?[y/n default y]"
		v_temp=`check`
		
		if [[ ${v_temp}X = X ]]; then
			#statements
			installVoltdb y true
		else
			installVoltdb $v_temp true	
		fi	
	else
		installVoltdb y false
	fi

	setfile
}

check()
{
    read answer

    temp=$(echo $answer | tr '[a-z]' '[A-Z]') 

    if [[ ${temp} != "" && ${temp} != "Y" && ${temp} != "N" ]]; then
    	#statements
    	check "$1"
    fi
    echo $temp
}

installJdk()
{

	info=`tar xvf $HOME/jdk/jdk1.6.0_24.tar -C ${v_jdk_install_path}`

	if [[ $? -ne 0 ]]; then
		#statements
		rm -rf $v_jdk_install_path/jdk1.6.0_24
		echo 'deploy JDK file is failed.'
	else
		cp $HOME/cfg/bashrc.ini $HOME/cfg/bashrc.ini.back
		sed "s/jdk1.8.0_65/jdk1.6.0_24/g" $HOME/cfg/bashrc.ini.back > $HOME/cfg/bashrc.ini
		rm -rf $HOME/cfg/bashrc.ini.back
		echo "deploy JDK file is successful."
	fi

}

installVoltdb()
{

	if [[ $1 = 'n' || $1 = 'N' || ${1}X = X ]]; then
		#statements
		return;			
	fi
	#echo $1
	#directory exist
	if [[ $2 = 'true' ]]; then
		rm -rf $HOME/$v_voltdb
	fi

	info=`tar zxvf $HOME/db/LINUX-voltdb-3.6.tar.gz -C ~`
	if [[ $? -eq 0 ]]; then
		#statements
		mv ~/voltdb-3.6 $v_voltdb
		echo "voltdb install successful."
	else
		echo "voltdb install failed."
	fi
	

}

setfile()
{

	if [[ ! -d $HOME/cfg ]]; then
		#statements
		cp cfg ~
	fi

	dos2unix $HOME/cfg/bashrc.ini

	chmod +x $HOME/ailib/*.sh

	vdir=`cat $HOME/ailib/deployment.xml | grep voltdbroot | awk -F '"' '{print $2}'`

	mkdir -p $vdir

	info=`grep -i "source $HOME/cfg/bashrc.ini" .bash_profile | wc -l`

	if [[ $info == 0 ]]; then
		#statements
		echo "source $HOME/cfg/bashrc.ini" >> .bash_profile		
	fi
	source ~/.bash_profile
}

clean()
{
	#rm -rf $HOME/ailib
	rm -rf $HOME/ai-voltdb.zip
	rm -rf $HOME/db
	rm -rf $HOME/jdk
	rm -rf $HOME/deploy.sh
}

main

clean