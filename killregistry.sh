kill $(ps ax | grep [r]mir | awk '{print $1}')
rm serverLogfile*
